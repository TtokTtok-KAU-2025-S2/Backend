package TtokTtok.Backend.external.openai;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.config.OpenAiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper; // ✨ 파싱을 위해 추가

    public OpenAiClient(RestClient.Builder builder, OpenAiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper; // ✨ 주입
        this.restClient = builder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .build();
    }

    /**
     * Whisper (audio transcription) 호출
     */
    public String transcribe(byte[] audioBytes, String filename, MediaType mediaType) {
        ByteArrayResource resource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", resource)
                .contentType(mediaType != null ? mediaType : MediaType.APPLICATION_OCTET_STREAM);
        bodyBuilder.part("model", properties.getTranscriptionModel());
        bodyBuilder.part("response_format", "text"); // Text로 받음
        bodyBuilder.part("temperature", "0");
        bodyBuilder.part("language", "ko");

        try {
            String responseText = restClient.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(bodyBuilder.build())
                    .retrieve()
                    .body(String.class);

            if (!StringUtils.hasText(responseText)) {
                log.error("OpenAI transcription returned empty response");
                throw new GeneralException(ErrorStatus.OPENAI_TRANSCRIPTION_FAILED);
            }
            return responseText;

        } catch (RestClientException e) {
            log.error("OpenAI transcription API error: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.OPENAI_TRANSCRIPTION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected transcription error", e);
            throw new GeneralException(ErrorStatus.OPENAI_TRANSCRIPTION_FAILED);
        }
    }

    /**
     * ChatCompletion 호출
     * ✨ 수정: String으로 받아서 수동 파싱 (자동 매핑 에러 방지)
     */
    public String createChatCompletion(OpenAiChatRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", properties.getChatModel());
        payload.put("messages", List.of(
                Map.of("role", "system", "content", request.systemPrompt()),
                Map.of("role", "user", "content", request.userPrompt())
        ));
        payload.put("temperature", Optional.ofNullable(request.temperature()).orElse(0.2));

        if (request.maxTokens() != null) {
            payload.put("max_tokens", request.maxTokens());
        }

        if (!CollectionUtils.isEmpty(request.responseFormat())) {
            payload.put("response_format", request.responseFormat());
        }

        try {
            // 1. String으로 응답 받기
            String responseBody = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            // 2. JSON 파싱 (ObjectMapper 사용)
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = rootNode.path("choices");

            if (choicesNode.isMissingNode() || choicesNode.isEmpty()) {
                log.error("OpenAI completion returned empty choices");
                throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
            }

            // 3. content 추출
            String content = choicesNode.get(0).path("message").path("content").asText();

            if (!StringUtils.hasText(content)) {
                throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
            }
            return content;

        } catch (RestClientException e) {
            log.error("OpenAI completion API error: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected completion error", e);
            throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
        }
    }

    @Builder
    public record OpenAiChatRequest(
            String systemPrompt,
            String userPrompt,
            Map<String, Object> responseFormat,
            Double temperature,
            Integer maxTokens
    ) {
    }
}
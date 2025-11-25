package TtokTtok.Backend.external.openai;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.config.OpenAiProperties;
import lombok.Builder;
import lombok.Getter;
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

    public OpenAiClient(RestClient.Builder builder, OpenAiProperties properties) {
        this.properties = properties;
        this.restClient = builder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .build();
    }

    /**
     * Whisper (audio transcription) 호출
     * Whisper 모델을 사용하여 음성 파일을 텍스트로 변환합니다.
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
        bodyBuilder.part("model", properties.getTranscriptionModel()); // whisper-1
        bodyBuilder.part("response_format", "json");
        bodyBuilder.part("temperature", "0");
        bodyBuilder.part("language", "ko"); // 한국어 지정 (선택사항이지만 정확도 향상)

        try {
            OpenAiTranscriptionResponse response = restClient.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(bodyBuilder.build())
                    .retrieve()
                    .body(OpenAiTranscriptionResponse.class);

            if (response == null || !StringUtils.hasText(response.getText())) {
                log.error("OpenAI transcription returned empty response");
                throw new GeneralException(ErrorStatus.OPENAI_TRANSCRIPTION_FAILED);
            }
            return response.getText();
        } catch (RestClientException e) {
            log.error("OpenAI transcription API error: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.OPENAI_TRANSCRIPTION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected transcription error", e);
            throw new GeneralException(ErrorStatus.OPENAI_TRANSCRIPTION_FAILED);
        }
    }

    /**
     * ChatCompletion 호출 (system/user 메시지 기반)
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
            OpenAiChatCompletionResponse response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(OpenAiChatCompletionResponse.class);

            if (response == null || CollectionUtils.isEmpty(response.getChoices())) {
                log.error("OpenAI completion returned empty choices");
                throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
            }

            String content = Optional.ofNullable(response.getChoices().get(0).getMessage())
                    .map(OpenAiChatCompletionResponse.Message::getContent)
                    .orElse(null);

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

    private static class OpenAiTranscriptionResponse {
        private String text;

        public String getText() {
            return text;
        }
    }

    @Getter
    private static class OpenAiChatCompletionResponse {
        private List<Choice> choices;

        @Getter
        private static class Choice {
            private Message message;
        }

        @Getter
        private static class Message {
            private String role;
            private String content;
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


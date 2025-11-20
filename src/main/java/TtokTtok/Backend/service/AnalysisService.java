package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.VoiceRecording;
import TtokTtok.Backend.external.openai.OpenAiClient;
import TtokTtok.Backend.external.openai.OpenAiClient.OpenAiChatRequest;
import TtokTtok.Backend.repository.RecordingRepository;
import TtokTtok.Backend.config.AmazonConfig;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.web.dto.ai.NoiseAiRequest;
import TtokTtok.Backend.web.dto.ai.NoiseAiResponse;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * AI 분석 파이프라인: S3 -> OpenAI Whisper -> GPT
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String CATEGORY_SYSTEM_PROMPT = """
            당신은 층간소음 패턴을 분류하는 전문가입니다. 제공된 녹음 내용과 측정 데이터를 기반으로 가장 적합한 카테고리를 선택하세요.
            가능한 카테고리: FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN.
            """;
    private static final String SUMMARY_SYSTEM_PROMPT = """
            당신은 층간소음을 겪은 사용자의 일기를 대신 작성해주는 한국어 어시스턴트입니다.
            - 3~4문장 내외로 공감, 상황 설명, 느낀점, 요청을 자연스럽게 포함합니다.
            - 과장하거나 사실과 다른 내용을 추가하지 않습니다.
            - 존댓말을 사용하고, 구체적인 시간/데시벨 정보를 활용합니다.
            """;

    private final RecordingRepository recordingRepository;
    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public NoiseAiResponse.CategoryAnalysisDto analyzeCategory(NoiseAiRequest.CategoryRequest request) {
        VoiceRecording recording = getOwnedRecording(request.getRecordId());
        String transcript = transcribeRecording(recording);

        String userPrompt = buildCategoryUserPrompt(request, transcript);
        Map<String, Object> responseFormat = buildCategoryResponseFormat();

        String completion = openAiClient.createChatCompletion(
                OpenAiChatRequest.builder()
                        .systemPrompt(CATEGORY_SYSTEM_PROMPT)
                        .userPrompt(userPrompt)
                        .responseFormat(responseFormat)
                        .temperature(0.0)
                        .maxTokens(128)
                        .build()
        );

        CategoryDecisionResult decision = parseCategoryDecision(completion);

        return NoiseAiResponse.CategoryAnalysisDto.builder()
                .category(decision.category())
                .transcript(transcript)
                .reason(decision.reason())
                .build();
    }

    public NoiseAiResponse.SummaryDto generateSummary(NoiseAiRequest.SummaryRequest request) {
        VoiceRecording recording = getOwnedRecording(request.getRecordId());
        String transcript = resolveTranscript(request.getTranscript(), recording);

        String userPrompt = buildSummaryUserPrompt(request, transcript);
        String completion = openAiClient.createChatCompletion(
                OpenAiChatRequest.builder()
                        .systemPrompt(SUMMARY_SYSTEM_PROMPT)
                        .userPrompt(userPrompt)
                        .temperature(0.35)
                        .maxTokens(320)
                        .build()
        );

        String summary = sanitizeAssistantText(completion);

        return NoiseAiResponse.SummaryDto.builder()
                .category(request.getCategory())
                .transcript(transcript)
                .summary(summary)
                .build();
    }

    private VoiceRecording getOwnedRecording(Long recordId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        VoiceRecording recording = recordingRepository.findById(recordId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.VOICE_RECORDING_NOT_FOUND));

        User owner = recording.getUser();
        if (owner == null || !owner.getId().equals(currentUserId)) {
            throw new GeneralException(ErrorStatus.VOICE_RECORDING_FORBIDDEN);
        }
        return recording;
    }

    private String resolveTranscript(String existingTranscript, VoiceRecording recording) {
        if (StringUtils.hasText(existingTranscript)) {
            return existingTranscript;
        }
        return transcribeRecording(recording);
    }

    private String transcribeRecording(VoiceRecording recording) {
        byte[] audioBytes = downloadRecording(recording.getFileUrl());
        MediaType mediaType = guessMediaType(recording.getOriginalFileName());
        String filename = determineFileName(recording);
        return openAiClient.transcribe(audioBytes, filename, mediaType);
    }

    /**
     * S3에서 녹음 파일을 다운로드합니다.
     * fileUrl에서 S3 key를 추출하여 파일을 다운로드합니다.
     */
    private byte[] downloadRecording(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            log.error("fileUrl is empty, cannot download recording");
            throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        }

        String key = resolveS3Key(fileUrl);
        log.debug("Downloading audio from S3. bucket={}, key={}", amazonConfig.getBucket(), key);

        try (S3Object s3Object = amazonS3.getObject(amazonConfig.getBucket(), key);
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            
            byte[] audioBytes = inputStream.readAllBytes();
            log.debug("Successfully downloaded audio. size={} bytes", audioBytes.length);
            return audioBytes;
            
        } catch (IOException e) {
            log.error("Failed to read audio stream from S3. bucket={}, key={}, fileUrl={}", 
                    amazonConfig.getBucket(), key, fileUrl, e);
            throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        } catch (Exception e) {
            log.error("Failed to download audio from S3. bucket={}, key={}, fileUrl={}", 
                    amazonConfig.getBucket(), key, fileUrl, e);
            throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        }
    }

    /**
     * S3 URL에서 key를 추출합니다.
     * 다양한 S3 URL 형식을 지원합니다:
     * - https://bucket.s3.region.amazonaws.com/key
     * - https://s3.region.amazonaws.com/bucket/key
     * - https://bucket.s3-region.amazonaws.com/key
     * - 직접 key만 있는 경우
     */
    private String resolveS3Key(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            log.error("fileUrl is empty or null");
            throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        }

        String bucket = amazonConfig.getBucket();
        
        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();
            
            // URL이 정상적으로 파싱되고 path가 있는 경우
            if (StringUtils.hasText(path)) {
                // path에서 앞의 "/" 제거
                String key = path.startsWith("/") ? path.substring(1) : path;
                
                // path가 bucket으로 시작하는 경우 (s3.region.amazonaws.com/bucket/key 형식)
                if (key.startsWith(bucket + "/")) {
                    return key.substring(bucket.length() + 1);
                }
                
                // 이미 key만 있는 경우
                if (StringUtils.hasText(key)) {
                    return key;
                }
            }
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse fileUrl as URI: {}", fileUrl, e);
            // URI 파싱 실패 시 아래 로직으로 계속 진행
        }

        // URL에서 bucket 이름을 찾아서 그 뒤의 key를 추출
        // 형식: https://bucket.s3.region.amazonaws.com/key
        int bucketIndex = fileUrl.indexOf(bucket);
        if (bucketIndex >= 0) {
            // bucket 이름 다음의 "/" 또는 "?" 위치 찾기
            int startIdx = bucketIndex + bucket.length();
            
            // "/" 또는 "?" 찾기
            int slashIndex = fileUrl.indexOf("/", startIdx);
            int queryIndex = fileUrl.indexOf("?", startIdx);
            
            int endIndex = -1;
            if (slashIndex >= 0 && queryIndex >= 0) {
                endIndex = Math.min(slashIndex, queryIndex);
            } else if (slashIndex >= 0) {
                endIndex = slashIndex;
            } else if (queryIndex >= 0) {
                endIndex = queryIndex;
            }
            
            if (endIndex >= 0 && endIndex + 1 < fileUrl.length()) {
                String key = fileUrl.substring(endIndex + 1);
                // query parameter 제거
                int queryParamIndex = key.indexOf("?");
                if (queryParamIndex >= 0) {
                    key = key.substring(0, queryParamIndex);
                }
                if (StringUtils.hasText(key)) {
                    return key;
                }
            }
        }
        
        // 위의 모든 방법이 실패한 경우, fileUrl 자체를 key로 사용
        // (이미 key만 있는 경우)
        log.debug("Using fileUrl as-is for S3 key: {}", fileUrl);
        return fileUrl;
    }

    private MediaType guessMediaType(String originalFileName) {
        if (!StringUtils.hasText(originalFileName) || !originalFileName.contains(".")) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "mp3" -> MediaType.valueOf("audio/mpeg");
            case "wav" -> MediaType.valueOf("audio/wav");
            case "m4a" -> MediaType.valueOf("audio/mp4");
            case "aac" -> MediaType.valueOf("audio/aac");
            case "webm" -> MediaType.valueOf("audio/webm");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private String determineFileName(VoiceRecording recording) {
        if (StringUtils.hasText(recording.getOriginalFileName())) {
            return recording.getOriginalFileName();
        }
        return "voice-recording-" + recording.getId() + ".m4a";
    }

    private Map<String, Object> buildCategoryResponseFormat() {
        var categories = Arrays.stream(NoiseCategory.values())
                .map(Enum::name)
                .toList();

        Map<String, Object> properties = new HashMap<>();
        properties.put("category", Map.of(
                "type", "string",
                "enum", categories
        ));
        properties.put("reason", Map.of(
                "type", "string",
                "description", "카테고리를 선택한 간단한 이유"
        ));

        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", List.of("category", "reason"));
        schema.put("additionalProperties", false);

        return Map.of(
                "type", "json_schema",
                "json_schema", Map.of(
                        "name", "noise_category_schema",
                        "schema", schema
                )
        );
    }

    private String buildCategoryUserPrompt(NoiseAiRequest.CategoryRequest request, String transcript) {
        return """
                다음은 한 사용자가 녹음한 층간소음 내용입니다.
                녹음 내용을 분석하여 가장 적합한 소음 카테고리를 선택하세요.

                녹음 내용:
                %s
                """.formatted(transcript);
    }

    private String buildSummaryUserPrompt(NoiseAiRequest.SummaryRequest request, String transcript) {
        LocalDateTime occuredAt = request.getOccuredAt() != null ? request.getOccuredAt() : LocalDateTime.now();
        String grade = request.getNoiseGrade() != null ? request.getNoiseGrade().name() : "UNKNOWN";
        return """
                측정 시각: %s
                평균 데시벨: %s dB
                최대 데시벨: %s dB
                소음 지속 시간: %d초
                소음 등급: %s
                사용자가 선택한 소음 카테고리: %s
                사용자의 메모: %s

                녹음에서 추출한 내용:
                %s
                """.formatted(
                occuredAt.format(DISPLAY_DATE_TIME),
                request.getDbAvg(),
                request.getDbHigh(),
                request.getDuration(),
                grade,
                request.getCategory().name(),
                request.getDescription(),
                transcript
        );
    }

    private CategoryDecisionResult parseCategoryDecision(String completion) {
        try {
            CategoryDecisionPayload payload = objectMapper.readValue(completion, CategoryDecisionPayload.class);
            NoiseCategory category = normalizeCategory(payload.category());
            return new CategoryDecisionResult(category, payload.reason());
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OpenAI category response: {}", completion, e);
            throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
        }
    }

    private NoiseCategory normalizeCategory(String categoryText) {
        try {
            return NoiseCategory.valueOf(categoryText.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return NoiseCategory.UNKNOWN;
        }
    }

    private String sanitizeAssistantText(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new GeneralException(ErrorStatus.OPENAI_COMPLETION_FAILED);
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replace("```", "").trim();
        }
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() > 1) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private record CategoryDecisionPayload(String category, String reason) {
    }

    private record CategoryDecisionResult(NoiseCategory category, String reason) {
    }
}

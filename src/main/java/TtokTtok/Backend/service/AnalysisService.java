package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.domain.NoiseDiary;
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
 * AI 분석 파이프라인
 * - 카테고리 분류: OpenAI (JSON 포맷)
 * - 요약 생성: Google Gemini (텍스트 생성)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");

    private static final String CATEGORY_SYSTEM_PROMPT = """
            당신은 층간소음 패턴을 분류하는 전문가입니다. 제공된 녹음 내용과 측정 데이터를 기반으로 가장 적합한 카테고리를 선택하세요.
            가능한 카테고리: FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN.
            """;

    // ✨ [Gemini용] 객관적인 팩트 요약 프롬프트
    private static final String SUMMARY_SYSTEM_PROMPT = """
            당신은 층간소음 상황을 팩트 위주로 간결하게 요약하는 리포터입니다.
            주어진 데이터를 바탕으로 '언제, 얼마 동안, 어떤 소음이 발생했는지'를 한 문장으로 요약하세요.
            
            [작성 규칙]
            1. 불필요한 서술이나 감정적 표현(공감, 위로 등)은 절대 금지합니다.
            2. 날짜, 시간, 지속시간, 소음 종류를 반드시 포함하세요.
            3. 어미는 '~발생', '~함' 등으로 간결하게 끝맺으세요.
            
            [출력 예시]
            - 10월 28일 23:00경, 10분간 지속적인 쿵쿵거리는 발소리 소음 발생
            - 11월 05일 14:20경, 5분간 간헐적인 망치질 소음 발생
            """;

    private final RecordingRepository recordingRepository;
    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final OpenAiClient openAiClient; // 카테고리 분석용 (JSON 응답 필요 시 사용)
    private final GeminiService geminiService; // ✨ 요약 생성용 (Google Gemini)
    private final ObjectMapper objectMapper;

    // ----------------------------------------------------------------
    // ✨ [수정] Gemini를 사용한 내부 호출용 AI 요약 생성 메서드
    // ----------------------------------------------------------------
    public String generateSummaryForReport(NoiseDiary diary) {
        // 1. 사용자 데이터 프롬프트 구성
        String userPrompt = buildSummaryUserPromptFromEntity(diary);

        // 2. 시스템 프롬프트와 결합 (GeminiService.fromTextInput은 단일 텍스트를 받으므로)
        String finalPrompt = SUMMARY_SYSTEM_PROMPT + "\n\n" + userPrompt;

        // 3. Gemini 호출
        try {
            log.info("Gemini AI 요약 요청 시작 (Diary ID: {})", diary.getId());
            String completion = geminiService.fromTextInput(finalPrompt);
            log.info("Gemini AI 요약 완료: {}", completion);
            return sanitizeAssistantText(completion);
        } catch (Exception e) {
            log.error("Gemini AI 요약 생성 실패 (Diary ID: {}): {}", diary.getId(), e.getMessage());
            return null;
        }
    }

    // [프롬프트 빌더] NoiseDiary 엔티티 -> 텍스트
    private String buildSummaryUserPromptFromEntity(NoiseDiary diary) {
        LocalDateTime occuredAt = diary.getOccuredAt() != null ? diary.getOccuredAt() : LocalDateTime.now();

        // 카테고리 한글 변환
        String categoryKr = switch (diary.getCategory()) {
            case FOOTSTEPS -> "발소리(쿵쿵거림)";
            case HAMMERING -> "망치질/두드리는 소리";
            case FURNITURE -> "가구 끄는 소리";
            case MUSIC -> "악기/음악 소리";
            case UNKNOWN -> "알 수 없는 소음";
        };

        return """
                [요청 데이터]
                - 발생 시각: %s
                - 지속 시간: %d초
                - 소음 종류: %s
                - 평균 데시벨: %s dB
                - 사용자 메모: %s
                
                위 데이터를 바탕으로 한 줄 요약을 작성해 주세요.
                """.formatted(
                occuredAt.format(DISPLAY_DATE_TIME),
                diary.getDuration(),
                categoryKr,
                diary.getDbAvg(),
                (diary.getDescription() != null ? diary.getDescription() : "없음")
        );
    }

    // ----------------------------------------------------------------
    // [수정] 컨트롤러용 요약 메서드도 Gemini로 변경
    // ----------------------------------------------------------------
    public NoiseAiResponse.SummaryDto generateSummary(NoiseAiRequest.SummaryRequest request) {
        VoiceRecording recording = getOwnedRecording(request.getRecordId());
        String transcript = resolveTranscript(request.getTranscript(), recording);

        String userPrompt = buildSummaryUserPrompt(request, transcript);
        String finalPrompt = SUMMARY_SYSTEM_PROMPT + "\n\n" + userPrompt;

        // ✨ Gemini 호출로 변경
        String completion = geminiService.fromTextInput(finalPrompt);
        String summary = sanitizeAssistantText(completion);

        return NoiseAiResponse.SummaryDto.builder()
                .category(request.getCategory())
                .transcript(transcript)
                .summary(summary)
                .build();
    }

    // ----------------------------------------------------------------
    // 기존 메서드들 (카테고리 분석 등) - OpenAI 유지
    // ----------------------------------------------------------------

    public NoiseAiResponse.CategoryAnalysisDto analyzeCategory(NoiseAiRequest.CategoryRequest request) {
        VoiceRecording recording = getOwnedRecording(request.getRecordId());
        String transcript = transcribeRecording(recording);

        String userPrompt = buildCategoryUserPrompt(request, transcript);
        Map<String, Object> responseFormat = buildCategoryResponseFormat();

        // 카테고리 분석은 JSON 포맷팅이 필요하므로 OpenAI 유지
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

    // ... (이하 private helper 메서드들은 변경 없음) ...

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

    private String resolveS3Key(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            log.error("fileUrl is empty or null");
            throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        }

        String bucket = amazonConfig.getBucket();

        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();

            if (StringUtils.hasText(path)) {
                String key = path.startsWith("/") ? path.substring(1) : path;
                if (key.startsWith(bucket + "/")) {
                    return key.substring(bucket.length() + 1);
                }
                if (StringUtils.hasText(key)) {
                    return key;
                }
            }
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse fileUrl as URI: {}", fileUrl, e);
        }

        int bucketIndex = fileUrl.indexOf(bucket);
        if (bucketIndex >= 0) {
            int startIdx = bucketIndex + bucket.length();
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
                int queryParamIndex = key.indexOf("?");
                if (queryParamIndex >= 0) {
                    key = key.substring(0, queryParamIndex);
                }
                if (StringUtils.hasText(key)) {
                    return key;
                }
            }
        }
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
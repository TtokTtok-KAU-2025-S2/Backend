package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.VoiceRecording;
import TtokTtok.Backend.repository.RecordingRepository;
import TtokTtok.Backend.config.AmazonConfig;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.web.dto.ai.NoiseAiRequest;
import TtokTtok.Backend.web.dto.ai.NoiseAiResponse;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * AI 분석 파이프라인 (전면 Gemini 전환)
 * - 카테고리 분류 & STT: Google Gemini (멀티모달)
 * - 요약 생성: Google Gemini (텍스트)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm");

    // 오디오 분석용 통합 프롬프트 (분류 + STT + 이유)
    private static final String AUDIO_ANALYSIS_PROMPT = """
            이 오디오 파일을 듣고 다음 작업을 수행하세요.
            
            1. [전사]: 오디오 내용을 텍스트로 받아쓰세요. (말소리가 없으면 소리 특징을 묘사하세요 ex: 쿵쿵거리는 소리)
            2. [분류]: 소리를 다음 카테고리 중 하나로 분류하세요.
               - FOOTSTEPS (발소리, 쿵쿵, 걷는 소리)
               - HAMMERING (망치질, 못 박는 소리, 둔탁한 타격음)
               - FURNITURE (가구 끄는 소리, 의자 끄는 소리)
               - MUSIC (악기 연주, 음악 소리, 베이스 울림)
               - VOICE (사람 말소리, 고성방가, 비명, 웃음소리)
               - PET (개 짖는 소리, 고양이 울음소리)
               - APPLIANCE (청소기, 세탁기, 건조기, 안마의자 소리)
               - DOOR (문 쾅 닫는 소리, 현관문 도어락 소리)
               - WATER (물 내리는 소리, 샤워 물소리, 배수관 소음)
               - CONSTRUCTION (인테리어 공사, 드릴, 전기톱 소리)
               - EXERCISE (런닝머신, 덤벨/바벨 놓는 소리, 홈트레이닝)
               - UNKNOWN (위 항목에 해당하지 않거나 식별 불가)
            3. [이유]: 분류한 이유를 간략히 설명하세요.
            
            반드시 아래 JSON 형식으로만 응답하세요 (Markdown 없이 JSON만 출력).
            {
              "transcript": "전사 내용",
              "category": "카테고리(영어)",
              "reason": "이유"
            }
            """;

    // 요약용 프롬프트
    private static final String SUMMARY_SYSTEM_PROMPT = """
            당신은 층간소음 상황을 팩트 위주로 간결하게 요약하는 리포터입니다.
            주어진 데이터를 바탕으로 '언제, 얼마 동안, 어떤 소음이 발생했는지'를 한 문장으로 요약하세요.
            [작성 규칙]
            1. 불필요한 서술이나 감정적 표현은 금지.
            2. 날짜, 시간, 지속시간, 소음 종류 포함.
            3. 어미는 '~발생', '~함' 등으로 간결하게.
            """;

    private final RecordingRepository recordingRepository;
    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final GeminiService geminiService; // ✨ OpenAI 제거, Gemini만 사용
    private final ObjectMapper objectMapper;

    // ----------------------------------------------------------------
    // ✨ [핵심 수정] ID 기반 카테고리 분석 (Gemini 멀티모달 적용)
    // ----------------------------------------------------------------
    public NoiseAiResponse.CategoryAnalysisDto analyzeCategory(NoiseAiRequest.CategoryRequest request) {
        // 1. 녹음 파일 조회 (ID 기반)
        VoiceRecording recording = getOwnedRecording(request.getRecordId());

        log.info("녹음 파일 Gemini 분석 시작 (ID: {})", recording.getId());

        // 2. S3에서 파일 다운로드 (byte[])
        byte[] audioBytes = downloadRecording(recording.getFileUrl());
        String mimeType = guessMimeType(recording.getOriginalFileName());

        // 3. ✨ Gemini에게 오디오 + 프롬프트 전송
        // (Whisper, GPT 과정을 Gemini 호출 한 번으로 통합)
        String jsonResponse = geminiService.analyzeAudio(audioBytes, mimeType, AUDIO_ANALYSIS_PROMPT);

        // 4. 응답 파싱
        AudioAnalysisResult result = parseAnalysisResult(jsonResponse);

        // 5. DTO 반환
        return NoiseAiResponse.CategoryAnalysisDto.builder()
                .category(result.category())
                .transcript(result.transcript())
                .reason(result.reason())
                // 메타데이터 매핑
                .createdAt(recording.getCreatedAt())
                .duration(recording.getDuration())
                .dbMax(recording.getDbMax())
                .dbAvg(recording.getDbAvg())
                .build();
    }

    // ----------------------------------------------------------------
    // 요약 생성 (기존 유지)
    // ----------------------------------------------------------------
    public String generateSummaryForReport(NoiseDiary diary) {
        String userPrompt = buildSummaryUserPromptFromEntity(diary);
        try {
            String completion = geminiService.fromTextInput(SUMMARY_SYSTEM_PROMPT + "\n\n" + userPrompt);
            return sanitizeAssistantText(completion);
        } catch (Exception e) {
            log.error("Gemini AI 요약 생성 실패: {}", e.getMessage());
            return null;
        }
    }

    public NoiseAiResponse.SummaryDto generateSummary(NoiseAiRequest.SummaryRequest request) {
        String userPrompt = buildSummaryUserPromptFromEntity(NoiseDiary.builder()
                .occuredAt(request.getOccuredAt())
                .duration(request.getDuration())
                .category(request.getCategory())
                .dbAvg(request.getDbAvg())
                .description(request.getDescription())
                .build());

        String completion = geminiService.fromTextInput(SUMMARY_SYSTEM_PROMPT + "\n\n" + userPrompt);
        return NoiseAiResponse.SummaryDto.builder()
                .category(request.getCategory())
                .summary(sanitizeAssistantText(completion))
                .build();
    }

    // --- Helper Methods ---

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

    // JSON 파싱 헬퍼
    private AudioAnalysisResult parseAnalysisResult(String jsonResponse) {
        try {
            String cleanJson = sanitizeAssistantText(jsonResponse);
            AudioAnalysisPayload payload = objectMapper.readValue(cleanJson, AudioAnalysisPayload.class);

            NoiseCategory category;
            try {
                category = NoiseCategory.valueOf(payload.category().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                category = NoiseCategory.UNKNOWN;
            }
            return new AudioAnalysisResult(category, payload.transcript(), payload.reason());
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", jsonResponse, e);
            return new AudioAnalysisResult(NoiseCategory.UNKNOWN, "분석 실패", "응답 형식을 인식할 수 없습니다.");
        }
    }

    private String sanitizeAssistantText(String raw) {
        if (!StringUtils.hasText(raw)) return "";
        String trimmed = raw.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.replace("```json", "");
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.replace("```", "");
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }

    private byte[] downloadRecording(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        String key = resolveS3Key(fileUrl);
        try (S3Object s3Object = amazonS3.getObject(amazonConfig.getBucket(), key);
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AUDIO_DOWNLOAD_FAILED);
        }
    }

    private String resolveS3Key(String fileUrl) {
        String bucket = amazonConfig.getBucket();
        try {
            URI uri = URI.create(fileUrl);
            String path = uri.getPath();
            if (StringUtils.hasText(path)) {
                String key = path.startsWith("/") ? path.substring(1) : path;
                if (key.startsWith(bucket + "/")) return key.substring(bucket.length() + 1);
                return key;
            }
        } catch (Exception e) {}
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    private String guessMimeType(String originalFileName) {
        if (!StringUtils.hasText(originalFileName) || !originalFileName.contains(".")) return "audio/mpeg"; // default
        String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "m4a" -> "audio/mp4";
            case "aac" -> "audio/aac";
            case "webm" -> "audio/webm";
            case "ogg" -> "audio/ogg";
            default -> "audio/mpeg";
        };
    }

    private String buildSummaryUserPromptFromEntity(NoiseDiary diary) {
        LocalDateTime occuredAt = diary.getOccuredAt() != null ? diary.getOccuredAt() : LocalDateTime.now();
        return """
                [데이터]
                - 발생 시각: %s
                - 지속 시간: %d초
                - 소음 종류: %s
                - 평균 데시벨: %s dB
                - 사용자 메모: %s
                """.formatted(
                occuredAt.format(DISPLAY_DATE_TIME),
                diary.getDuration(),
                diary.getCategory(),
                diary.getDbAvg(),
                (diary.getDescription() != null ? diary.getDescription() : "없음")
        );
    }

    // DTO for internal parsing
    private record AudioAnalysisPayload(String transcript, String category, String reason) {}
    private record AudioAnalysisResult(NoiseCategory category, String transcript, String reason) {}
}
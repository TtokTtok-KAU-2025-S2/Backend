package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class KnockResponseDto {

    /**
     * '똑똑' 생성 응답
     */
    @Builder @Getter
    @NoArgsConstructor @AllArgsConstructor
    public static class CreateResultDto {
        private Long requestId;
        private LocalDateTime requestTime;
        private String noiseCategory;
    }

    /**
     * '똑똑' 응답하기 응답
     */
    @Builder @Getter
    @NoArgsConstructor @AllArgsConstructor
    public static class RespondResultDto {
        private Long responseId;
        private Long requestId;
        private Long responderId;
        private ResponseType responseType;
    }

    /**
     * '똑똑' 결과 조회 응답
     */
    @Builder @Getter
    @NoArgsConstructor @AllArgsConstructor
    public static class KnockResultDto {
        private Long requestId;
        private String noiseCategory;
        private LocalDateTime requestTime;
        private Boolean isActive;
        private Integer totalResponder;
        private Integer heardCount;
        private Integer quietCount;
        private Integer peacefulCount;
    }

    @Builder @Getter
    @NoArgsConstructor @AllArgsConstructor
    public static class ReportResultDto {
        private Long reportId; // 보고 ID
        private Long reportedRequestId; // 보고된 요청 ID
        private LocalDateTime reportedAt; // 보고된 시간
    }
}
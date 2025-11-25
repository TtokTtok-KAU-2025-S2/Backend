package TtokTtok.Backend.web.dto.ai;

import TtokTtok.Backend.common.enums.NoiseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class NoiseAiResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryAnalysisDto {
        private NoiseCategory category;
        private String transcript;
        private String reason;

        // ✨ [추가] 요청하신 상세 정보 필드
        private LocalDateTime createdAt; // 측정 시간 (날짜)
        private Integer duration;        // 측정 시간 (초)
        private Double dbMax;            // 최대 소음
        private Double dbAvg;            // 평균 소음
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDto {
        private NoiseCategory category;
        private String transcript;
        private String summary;
    }
}
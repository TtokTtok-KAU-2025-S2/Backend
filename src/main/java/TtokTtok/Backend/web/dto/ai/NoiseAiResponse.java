package TtokTtok.Backend.web.dto.ai;

import TtokTtok.Backend.common.enums.NoiseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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



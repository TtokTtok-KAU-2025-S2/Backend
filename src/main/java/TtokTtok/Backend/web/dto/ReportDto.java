package TtokTtok.Backend.web.dto; // 수정된 패키지

import TtokTtok.Backend.common.enums.NoiseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ReportDto {

    /**
     * 카테고리별 통계 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class CategoryStatDto {
        private NoiseCategory category;
        private Long count;
    }

    /**
     * 시간대별 통계 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class HourlyStatDto {
        private Integer hour; // 0-23
        private Long count;
    }
}
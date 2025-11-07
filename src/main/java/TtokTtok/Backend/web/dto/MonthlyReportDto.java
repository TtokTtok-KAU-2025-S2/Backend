package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.domain.MonthlyReport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class MonthlyReportDto {

    /**
     * 월간 리포트 상세 조회 응답 DTO
     */
    @Getter
    @Builder
    public static class MonthlyReportResponse {
        // "2024년 10월"
        private String reportTitle;

        // "이달의 소음 리포트" (e.g., 47건)
        private Integer totalReportCount;

        // "전월 대비 감소" (e.g., -12.0)
        private BigDecimal changeRate;

        // "AI 분석 요약" (e.g., "이번 달 우리 아파트의 소음 민원은...")
        private String aiAnalysisText;

        // "주요 소음 발생 시간대" (JSON -> Map 변환)
        private Map<String, Long> hourlyStats;

        // "소음 유형 분포" (JSON -> Map 변환)
        private Map<String, Long> categoryStats;

        // ------------------------------------
        // [Converter] Entity -> DTO 변환
        // ------------------------------------
        public static MonthlyReportResponse fromEntity(MonthlyReport report, ObjectMapper objectMapper) {
            Map<String, Long> hourlyStats = parseJsonToMap(report.getHourlyStatsJson(), objectMapper);
            Map<String, Long> categoryStats = parseJsonToMap(report.getCategoryStatsJson(), objectMapper);

            return MonthlyReportResponse.builder()
                    .reportTitle(report.getYear() + "년 " + report.getMonth() + "월")
                    .totalReportCount(report.getTotalReportCount())
                    .changeRate(report.getChangeRate())
                    .aiAnalysisText(report.getAiAnalysisText())
                    .hourlyStats(hourlyStats)
                    .categoryStats(categoryStats)
                    .build();
        }

        // JSON 문자열을 Map<String, Long>으로 변환하는 헬퍼 메소드
        private static Map<String, Long> parseJsonToMap(String json, ObjectMapper objectMapper) {
            if (json == null || json.isEmpty()) {
                return Map.of(); // 비어있으면 빈 맵 반환
            }
            try {
                // Key가 숫자(시간)나 Enum 문자열일 수 있으므로 Map<String, Long>으로 통일
                return objectMapper.readValue(json, new TypeReference<Map<String, Long>>() {});
            } catch (JsonProcessingException e) {
                log.warn("월간 리포트 JSON 파싱 실패: {}", json, e);
                return Map.of(); // 파싱 실패 시 빈 맵 반환
            }
        }
    }
}
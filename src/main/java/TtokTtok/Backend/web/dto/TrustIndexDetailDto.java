package TtokTtok.Backend.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class TrustIndexDetailDto {
    private Double currentScore;
    //private String grade;
    private String rating;
    private List<MonthlyTrendDto> monthlyTrends;
    private List<RecentActivityDto> recentActivities;

    @Builder
    @Getter
    public static class MonthlyTrendDto {
        private String monthLabel;
        private Double score;
        private Double change;
    }

    @Builder
    @Getter
    public static class RecentActivityDto {
        private String description;
        private LocalDateTime dateTime; // date -> dateTime, LocalDate -> LocalDateTime (시간까지 표시할 경우)
        // private LocalDate date; // 날짜만 표시할 경우 기존 유지
        private Double changeAmount;
    }
}

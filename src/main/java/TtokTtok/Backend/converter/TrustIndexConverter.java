package TtokTtok.Backend.converter;

import TtokTtok.Backend.domain.user.TrustIndex;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.repository.TrustIndexRepository;
import TtokTtok.Backend.web.dto.TrustIndexDetailDto;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrustIndexConverter {

    public static TrustIndexDetailDto toTrustIndexDetailDto(User user,
                                                            List<TrustIndexRepository.MonthlyTrustChange> monthlyChanges,
                                                            List<TrustIndex> recentLogs) { // 엔티티 타입 변경

        Double currentScore = user.getTrustIndex();
        //String grade = calculateGrade(currentScore);
        String rating = calculateRating(currentScore);

        List<TrustIndexDetailDto.MonthlyTrendDto> monthlyTrendDtos = calculateMonthlyTrends(currentScore, monthlyChanges);

        // 최근 활동 DTO 생성 (엔티티 타입 변경 및 필드명 변경 반영)
        List<TrustIndexDetailDto.RecentActivityDto> recentActivityDtos = recentLogs.stream()
                .map(log -> TrustIndexDetailDto.RecentActivityDto.builder()
                        .description(log.getDescription())
                        .dateTime(log.getLogDate()) // date -> dateTime, LocalDate -> LocalDateTime
                        // .date(log.getLogDate().toLocalDate()) // 날짜만 필요하면 이 코드 사용
                        .changeAmount(log.getChangeAmount())
                        .build())
                .collect(Collectors.toList());

        return TrustIndexDetailDto.builder()
                .currentScore(currentScore)
                //.grade(grade)
                .rating(rating)
                .monthlyTrends(monthlyTrendDtos)
                .recentActivities(recentActivityDtos)
                .build();
    }

    // --- 평가 계산 로직 수정 (50점부터 '양호') ---
    private static String calculateRating(Double score) {
        if (score == null) return "정보 없음";
        if (score >= 90) return "최우수";
        if (score >= 70) return "우수";   // 80 -> 70
        if (score >= 50) return "양호";   // 70 -> 50 (50점부터 양호 시작)
        return "주의";                 // 50점 미만 주의
    }
    // --- 수정 종료 ---

    // 월별 누적 점수 계산 로직
    private static List<TrustIndexDetailDto.MonthlyTrendDto> calculateMonthlyTrends(
            Double currentScore, List<TrustIndexRepository.MonthlyTrustChange> monthlyChanges) {

        List<TrustIndexDetailDto.MonthlyTrendDto> trends = new ArrayList<>();
        Map<YearMonth, Double> changeMap = new HashMap<>();
        if (monthlyChanges != null) { // Null 체크 추가
            for (TrustIndexRepository.MonthlyTrustChange change : monthlyChanges) {
                if (change.getYear() != null && change.getMonth() != null && change.getTotalChange() != null) { // Null 체크
                    changeMap.put(YearMonth.of(change.getYear(), change.getMonth()), change.getTotalChange());
                }
            }
        }

        YearMonth currentMonth = YearMonth.now();
        Double scoreCursor = (currentScore != null) ? currentScore : 50.0; // 현재 점수가 null이면 50점으로 간주

        for (int i = 0; i < 6; i++) {
            YearMonth ym = currentMonth.minusMonths(i);
            Double change = changeMap.getOrDefault(ym, 0.0);

            Double monthScore;
            if (i == 0) {
                monthScore = scoreCursor; // 현재 월은 현재 점수
                scoreCursor -= change; // 다음 루프(과거) 계산을 위해 현재 월 변동량 미리 빼기
            } else {
                monthScore = scoreCursor; // 과거 월 점수는 이전 루프에서 계산된 커서 값
                scoreCursor -= change; // 다음 과거 월 계산 위해 커서 이동
            }

            trends.add(0, TrustIndexDetailDto.MonthlyTrendDto.builder()
                    .monthLabel(ym.format(DateTimeFormatter.ofPattern("M월")))
                    .score(monthScore)
                    .change(change)
                    .build());
        }
        return trends;
    }
}

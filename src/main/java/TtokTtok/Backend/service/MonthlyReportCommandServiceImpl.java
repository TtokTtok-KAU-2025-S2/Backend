package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.MonthlyReport;
import TtokTtok.Backend.repository.ApartmentRepository;
import TtokTtok.Backend.repository.MonthlyReportRepository;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.web.dto.ReportDto; // 수정된 import
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MonthlyReportCommandServiceImpl implements MonthlyReportCommandService {

    private final ApartmentRepository apartmentRepository;
    private final NoiseDiaryRepository noiseDiaryRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    @Override
    public void generateMonthlyReports(LocalDate targetDate) {
        log.info("월간 리포트 생성 시작: {}" , targetDate);
        List<Apartment> allApartments = apartmentRepository.findAll();

        for (Apartment apartment : allApartments) {
            try {
                generateReportForApartment(apartment, targetDate.getYear(), targetDate.getMonthValue());
            } catch (Exception e) {
                log.error("리포트 생성 실패 (아파트 ID: {}): {}" , apartment.getId(), e.getMessage());
            }
        }
        log.info("월간 리포트 생성 완료");
    }

    private void generateReportForApartment(Apartment apartment, int year, int month) throws Exception {

        // 1. 기간 설정
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusNanos(1);

        // 2. 총 건수
        Integer totalCount = noiseDiaryRepository.countByApartmentAndReportedAtBetween(apartment, startTime, endTime);

        // 3. 전월 건수
        LocalDateTime prevStartTime = startTime.minusMonths(1);
        LocalDateTime prevEndTime = endTime.minusMonths(1);
        Integer prevTotalCount = noiseDiaryRepository.countByApartmentAndReportedAtBetween(apartment, prevStartTime, prevEndTime);

        // 4. 증감률 계산
        BigDecimal changeRate = calculateChangeRate(totalCount, prevTotalCount);

        // 5. 통계 쿼리 (시간/카테고리)
        List<ReportDto.HourlyStatDto> hourlyStats = noiseDiaryRepository.findHourlyStatsByApartmentAndReportedAtBetween(apartment, startTime, endTime);
        List<ReportDto.CategoryStatDto> categoryStats = noiseDiaryRepository.findCategoryStatsByApartmentAndReportedAtBetween(apartment, startTime, endTime);

        // 6. JSON 변환
        String hourlyStatsJson = convertMapToJson(
                hourlyStats.stream().collect(Collectors.toMap(ReportDto.HourlyStatDto::getHour, ReportDto.HourlyStatDto::getCount))
        );
        String categoryStatsJson = convertMapToJson(
                categoryStats.stream().collect(Collectors.toMap(dto -> dto.getCategory().name(), ReportDto.CategoryStatDto::getCount))
        );

        // 7. AI 프롬프트 생성
        String aiPromptData = buildAiPromptData(totalCount, changeRate, hourlyStats, categoryStats);

        // ------------------------------------
// [AI 디버깅용 로그 추가]
// ------------------------------------
        log.info("===== AI 프롬프트 전송 데이터 (Apt ID: {}) =====", apartment.getId());
        log.info(aiPromptData);
        log.info("==========================================");

        // 8. Gemini AI 호출
        String aiSummary = aiService.getMonthlyReportSummary(aiPromptData);

        // 9. 엔티티 생성
        MonthlyReport report = MonthlyReport.builder()
                .apartment(apartment)
                .year(year)
                .month(month)
                .totalReportCount(totalCount)
                .changeRate(changeRate)
                .hourlyStatsJson(hourlyStatsJson)
                .categoryStatsJson(categoryStatsJson)
                .aiAnalysisText(aiSummary)
                .build();

        // 10. DB 저장
        monthlyReportRepository.save(report);
    }

    // --- Helper Methods ---

    private String buildAiPromptData(Integer totalCount, BigDecimal changeRate, List<ReportDto.HourlyStatDto> hourlyStats, List<ReportDto.CategoryStatDto> categoryStats) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("총 소음 리포트 건수: %d건\n", totalCount));
        sb.append(String.format("전월 대비 증감률: %.2f%%\n", changeRate));

        String topCategory = categoryStats.stream()
                .max((a, b) -> b.getCount().compareTo(a.getCount()))
                .map(s -> s.getCategory().name())
                .orElse("데이터 없음");
        sb.append(String.format("가장 빈번한 소음 유형: %s\n", topCategory));

        String topHour = hourlyStats.stream()
                .max((a, b) -> b.getCount().compareTo(a.getCount()))
                .map(s -> s.getHour() + "시 ~ " + (s.getHour() + 1) + "시")
                .orElse("데이터 없음");
        sb.append(String.format("가장 빈번한 소음 발생 시간대: %s\n", topHour));

        return sb.toString();
    }

    private BigDecimal calculateChangeRate(Integer totalCount, Integer prevTotalCount) {
        if (prevTotalCount == null || prevTotalCount == 0) {
            return (totalCount > 0) ? new BigDecimal("100.00") : BigDecimal.ZERO;
        }
        BigDecimal tc = new BigDecimal(totalCount);
        BigDecimal ptc = new BigDecimal(prevTotalCount);
        return tc.subtract(ptc)
                .divide(ptc, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String convertMapToJson(Map<?, ?> map) throws Exception {
        return objectMapper.writeValueAsString(map);
    }
}
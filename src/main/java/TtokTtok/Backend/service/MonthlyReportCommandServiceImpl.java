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
import java.util.Comparator; // ✨ Import 추가
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
    private final ObjectMapper objectMapper;

    // ------------------------------------
    // ✨ 1. GeminiService 주입
    // ------------------------------------
    private final GeminiService geminiService;


    @Override
    public void generateMonthlyReports(LocalDate targetDate) {
        log.info("월간 리포트 생성 시작: {}" , targetDate);
        List<Apartment> allApartments = apartmentRepository.findAll();

        for (Apartment apartment : allApartments) {
            try {
                generateReportForApartment(apartment, targetDate.getYear(), targetDate.getMonthValue());
            } catch (Exception e) {
                // ✨ 오류 로그에 스택 트레이스 추가
                log.error("리포트 생성 실패 (아파트 ID: {}): {}" , apartment.getId(), e.getMessage(), e);
            }
        }
        log.info("월간 리포트 생성 완료");
    }

    private void generateReportForApartment(Apartment apartment, int year, int month) throws Exception {

        // 1. 기간 설정
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = startTime.plusMonths(1).minusNanos(1);

        // 2. 총 건수 (report_yn=true이고 reportedAt이 해당 월인 데이터 기준)
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

        // 7. AI 프롬프트 생성 (AI에게 전달할 원본 데이터)
        String aiPromptData = buildAiPromptData(totalCount, changeRate, hourlyStats, categoryStats);

        // [AI 디버깅용 로그 추가]
        log.info("===== AI 프롬프트 전송 데이터 (Apt ID: {}) =====", apartment.getId());
        log.info(aiPromptData);
        log.info("==========================================");

        // ------------------------------------
        // ✨ 8. Gemini AI 호출 (로직 수정)
        // ------------------------------------
        String aiSummary;
        try {
            // AI에게 역할과 지시를 내리는 최종 프롬프트 생성
            String finalPrompt = buildFinalAiPrompt(year, month, apartment.getName(), aiPromptData);

            // GeminiService 호출
            aiSummary = geminiService.fromTextInput(finalPrompt);
            log.info("✅ AI 요약 생성 성공 (Apt ID: {})", apartment.getId());

        } catch (Exception e) {
            log.warn("🚨 AI 요약 생성 실패 (Apt ID: {}). 임시 텍스트로 대체합니다. 원인: {}", apartment.getId(), e.getMessage());
            // ❌ AI가 실패하면 이 임시 텍스트를 저장하고 다음 단계로 진행
            aiSummary = String.format("%d년 %d월 리포트 요약 생성에 실패했습니다. 관리자에게 문의하세요.", year, month);
        }

        // 9. 엔티티 생성
        MonthlyReport report = MonthlyReport.builder()
                .apartment(apartment)
                .year(year)
                .month(month)
                .totalReportCount(totalCount)
                .changeRate(changeRate)
                .hourlyStatsJson(hourlyStatsJson)
                .categoryStatsJson(categoryStatsJson)
                .aiAnalysisText(aiSummary) // AI가 생성했거나, 실패 시 임시 텍스트 저장
                .build();

        // 10. DB 저장
        monthlyReportRepository.save(report);
    }

    // --- Helper Methods ---

    // ✨ 헬퍼 메서드 추가: AI에게 역할을 부여하고 최종 지시를 내리는 프롬프트
    private String buildFinalAiPrompt(int year, int month, String aptName, String analysisData) {
        return String.format(
                "당신은 아파트 소음 관리 전문 AI 분석가입니다. " +
                        "다음은 '%s' 아파트의 %d년 %d월 한 달간 소음 현황판에 공지된(report_yn=true) 소음 통계 데이터입니다. " +
                        "이 데이터를 기반으로, 아파트 관리사무소와 입주민에게 전달할 간결하고 전문적인 분석 요약(100자 내외)을 작성해 주세요. " +
                        "가장 두드러진 특징(예: 전월 대비 증가, 특정 시간대/유형 집중)을 반드시 포함해야 합니다.\n\n" +
                        "--- 분석 데이터 ---\n" +
                        "%s",
                aptName, year, month, analysisData
        );
    }

    // ✨ 헬퍼 메서드 수정: AI가 더 풍부하게 분석할 수 있도록 상세 데이터 추가
    private String buildAiPromptData(Integer totalCount, BigDecimal changeRate, List<ReportDto.HourlyStatDto> hourlyStats, List<ReportDto.CategoryStatDto> categoryStats) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("총 소음 리포트 건수: %d건\n", totalCount));
        sb.append(String.format("전월 대비 증감률: %.2f%%\n", changeRate));

        // ✨ 버그 수정: max(Comparator.comparing(...))을 사용하여 최댓값을 올바르게 찾도록 수정
        String topCategory = categoryStats.stream()
                .max(Comparator.comparing(ReportDto.CategoryStatDto::getCount))
                .map(s -> s.getCategory().name())
                .orElse("데이터 없음");
        sb.append(String.format("가장 빈번한 소음 유형: %s\n", topCategory));

        // ✨ 버그 수정: max(Comparator.comparing(...))을 사용하여 최댓값을 올바르게 찾도록 수정
        String topHour = hourlyStats.stream()
                .max(Comparator.comparing(ReportDto.HourlyStatDto::getCount))
                .map(s -> s.getHour() + "시 ~ " + (s.getHour() + 1) + "시")
                .orElse("데이터 없음");
        sb.append(String.format("가장 빈번한 소음 발생 시간대: %s\n", topHour));

        // ✨ AI 분석 품질 향상을 위해 상세 데이터 추가
        sb.append("\n[상세 통계]\n");
        sb.append("시간대별 건수: " + hourlyStats.stream()
                .map(s -> String.format("%d시(%d건)", s.getHour(), s.getCount()))
                .collect(Collectors.joining(", ")) + "\n");
        sb.append("유형별 건수: " + categoryStats.stream()
                .map(s -> String.format("%s(%d건)", s.getCategory().name(), s.getCount()))
                .collect(Collectors.joining(", ")) + "\n");

        return sb.toString();
    }

    private BigDecimal calculateChangeRate(Integer totalCount, Integer prevTotalCount) {
        if (prevTotalCount == null || prevTotalCount == 0) {
            // 전월 0건 -> 당월 1건 이상이면 100% 증가로 표기 (0건 -> 0건이면 0)
            return (totalCount > 0) ? new BigDecimal("100.00") : BigDecimal.ZERO;
        }
        BigDecimal tc = new BigDecimal(totalCount);
        BigDecimal ptc = new BigDecimal(prevTotalCount);
        return tc.subtract(ptc)
                .divide(ptc, 4, RoundingMode.HALF_UP) // 소수점 4자리까지 계산
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP); // 최종 2자리로 반올림
    }

    private String convertMapToJson(Map<?, ?> map) throws Exception {
        return objectMapper.writeValueAsString(map);
    }
}
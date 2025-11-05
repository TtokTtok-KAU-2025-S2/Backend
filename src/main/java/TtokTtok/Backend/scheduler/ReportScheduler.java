package TtokTtok.Backend.scheduler;

import TtokTtok.Backend.service.ApartmentStatCommandService; // 1. (추가) ApartmentStatCommandService 임포트
import TtokTtok.Backend.service.MonthlyReportCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final MonthlyReportCommandService monthlyReportCommandService;
    private final ApartmentStatCommandService apartmentStatCommandService; // 2. (추가) 주입

    /**
     * 매월 1일 새벽 4시에 실행 (KST)
     * (수정) 월간 리포트 + 전국 아파트 통계 동시 생성
     */
    @Scheduled(cron = "0 0 4 1 * *", zone = "Asia/Seoul")
    public void runMonthlyBatch() { // 3. (수정) 메서드명 변경 (더 포괄적으로)
        log.info("===== 월간 리포트 및 전국 통계 자동 생성 스케줄러 시작 =====");

        // 기준 날짜 (예: 12월 1일 새벽 4시)
        LocalDate today = LocalDate.now();
        // 생성할 리포트의 기준 (예: 11월 1일)
        LocalDate targetMonth = today.minusMonths(1);

        // --- 1. 월간 리포트 생성 (AI 요약 포함) ---
        try {
            log.info("[배치 1/2] 월간 리포트 생성을 시작합니다. (기준: {}월)", targetMonth.getMonthValue());
            // CommandService는 11월 1일을 넘겨야 11월 리포트를 생성
            monthlyReportCommandService.generateMonthlyReports(targetMonth);
            log.info("[배치 1/2] 월간 리포트 생성 완료.");
        } catch (Exception e) {
            log.error("[배치 1/2] 월간 리포트 생성 실패.", e);
        }

        // --- 2. 전국 아파트 통계 생성 ---
        try {
            log.info("[배치 2/2] 전국 아파트 통계 생성을 시작합니다. (기준: {}월)", targetMonth.getMonthValue());
            // StatCommandService는 '오늘' 날짜를 넘기면 알아서 '지난달'을 계산
            apartmentStatCommandService.updateNationwideStats(today);
            log.info("[배치 2/2] 전국 아파트 통계 생성 완료.");
        } catch (Exception e) {
            log.error("[배치 2/2] 전국 아파트 통계 생성 실패.", e);
        }

        log.info("===== 월간 리포트 및 전국 통계 자동 생성 스케줄러 완료 =====");
    }
}
package TtokTtok.Backend.scheduler;

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

    /**
     * 매월 1일 새벽 4시에 실행 (KST)
     */
    @Scheduled(cron = "0 0 4 1 * *", zone = "Asia/Seoul")
    public void runMonthlyReportGeneration() {
        log.info("월간 리포트 스케줄러 실행");
        // 예: 11월 1일에 실행되면 -> 10월 리포트를 생성
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        monthlyReportCommandService.generateMonthlyReports(lastMonth);
    }
}
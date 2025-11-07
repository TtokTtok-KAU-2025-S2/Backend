package TtokTtok.Backend.service;

import java.time.LocalDate;

public interface MonthlyReportCommandService {
    /**
     * 모든 아파트를 대상으로 특정 월의 리포트를 생성합니다.
     * @param targetDate (예: 10월 리포트를 만들고 싶으면 10월 1일)
     */
    void generateMonthlyReports(LocalDate targetDate);
}

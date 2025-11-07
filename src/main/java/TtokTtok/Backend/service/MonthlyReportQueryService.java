package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.MonthlyReportDto;
import java.time.LocalDate;

public interface MonthlyReportQueryService {

    /**
     * (CommandService가 생성한) 월간 리포트를 조회합니다.
     */
    MonthlyReportDto.MonthlyReportResponse getMonthlyReport(Long apartmentId, LocalDate targetDate);
}
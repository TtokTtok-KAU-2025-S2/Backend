package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.MonthlyReportDto;

import java.time.LocalDate;

public interface MonthlyReportQueryService {

    /**
     * 특정 아파트의 특정 월 리포트를 조회합니다.
     * @param apartmentId 조회할 아파트 ID
     * @param targetDate 조회할 날짜 (이 날짜의 '월'을 기준으로 조회)
     * @return MonthlyReportResponse DTO
     */
    MonthlyReportDto.MonthlyReportResponse getMonthlyReport(Long apartmentId, LocalDate targetDate);
}
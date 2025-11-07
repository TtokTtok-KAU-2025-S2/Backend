package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.service.MonthlyReportQueryService;
import TtokTtok.Backend.web.dto.MonthlyReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports") // 정식 API 경로
public class MonthlyReportController {

    private final MonthlyReportQueryService monthlyReportQueryService;

    /**
     * 월간 리포트 조회 API
     *
     * @param apartmentId (필수) 조회할 아파트 ID
     * @param date (선택) 조회할 날짜 (YYYY-MM-DD). 미입력 시 '오늘' 날짜 기준
     * @return MonthlyReportResponse DTO
     */
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportDto.MonthlyReportResponse> getMonthlyReport(
            @RequestParam("aptId") Long apartmentId, // (TODO: JWT 인증 도입 시 @AuthUser로 대체 가능)
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 날짜 파라미터가 없으면 '오늘' 날짜를 기준으로 조회
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        // (보안) TODO: JWT 토큰에서 유저 정보를 꺼내,
        //       유저가 속한 아파트(apartmentId)의 리포트만 조회하도록 검증해야 함.

        MonthlyReportDto.MonthlyReportResponse response =
                monthlyReportQueryService.getMonthlyReport(apartmentId, targetDate);

        return ResponseEntity.ok(response);
    }
}
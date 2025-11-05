package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.service.MonthlyReportCommandService; // 1. CommandService 임포트
import TtokTtok.Backend.service.MonthlyReportQueryService;
import TtokTtok.Backend.service.ApartmentStatCommandService;
import TtokTtok.Backend.web.dto.MonthlyReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // 2. PostMapping 임포트
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class MonthlyReportController {

    private final MonthlyReportQueryService monthlyReportQueryService;
    private final MonthlyReportCommandService monthlyReportCommandService; // 3. CommandService 주입
    private final ApartmentStatCommandService apartmentStatCommandService;

    /**
     * 월간 리포트 조회 API (QueryService 호출)
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyReportDto.MonthlyReportResponse>> getMonthlyReport(
            @RequestParam("aptId") Long apartmentId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        // (QueryService가 MonthlyReport 테이블을 조회)
        MonthlyReportDto.MonthlyReportResponse response =
                monthlyReportQueryService.getMonthlyReport(apartmentId, targetDate);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // ---------------------------------------------------------------------
    // [임시 테스트용] 월간 리포트 수동 생성 배치 컨트롤러
    // (테스트 후 삭제하세요)
    // ---------------------------------------------------------------------
    @PostMapping("/admin/generate-batch") // 4. POST 메서드로 새 엔드포인트 추가
    public ResponseEntity<ApiResponse<String>> runBatch(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate
    ) {
        try {
            // 5. CommandService의 AI 배치 실행
            monthlyReportCommandService.generateMonthlyReports(targetDate);

            String message = targetDate.getYear() + "년 " + targetDate.getMonthValue() + "월 리포트 생성 완료";
            return ResponseEntity.ok(ApiResponse.onSuccess(message));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("500", "배치 실행 실패", e.getMessage()));
        }
    }

    // [임시 테스트용] 전국 아파트 통계 수동 생성
    // (테스트 후 이 메서드를 삭제하세요)
    // ---------------------------------------------------------------------
    @PostMapping("/admin/generate-stat-batch") // 4. (추가) "generate-stat-batch" 엔드포인트
    public ResponseEntity<ApiResponse<String>> runStatBatch(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate
    ) {
        try {
            // 5. (추가) ApartmentStatCommandService 실행
            apartmentStatCommandService.updateNationwideStats(targetDate);

            String message = targetDate.minusMonths(1).getMonthValue() + "월 기준 전국 통계 생성 완료";
            return ResponseEntity.ok(ApiResponse.onSuccess(message));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("500", "배치 실행 실패", e.getMessage()));
        }
    }
}
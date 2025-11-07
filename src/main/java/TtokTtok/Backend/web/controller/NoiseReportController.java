package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.service.NoiseReportService;
import TtokTtok.Backend.web.dto.NoiseReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/noise-reports")
@RequiredArgsConstructor
public class NoiseReportController {

    private final NoiseReportService noiseReportService;

    @GetMapping
    public ApiResponse<NoiseReportResponse.NoiseReportListResponse> getNoiseReportList(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) { // filter=my_dong
        Pageable pageable = PageRequest.of(page, size);
        NoiseReportResponse.NoiseReportListResponse response = noiseReportService.getNoiseReportList(pageable);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{reportId}")
    public ApiResponse<NoiseReportResponse.NoiseReportDetailDto> getNoiseReportDetail(
            @PathVariable Long reportId) {
        NoiseReportResponse.NoiseReportDetailDto response = noiseReportService.getNoiseReportDetail(reportId);
        return ApiResponse.onSuccess(response);
    }
}
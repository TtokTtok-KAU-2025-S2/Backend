package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.converter.KnockConverter;
import TtokTtok.Backend.domain.knock.KnockReport;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.knock.KnockResponse;
import TtokTtok.Backend.service.KnockCommandService;
import TtokTtok.Backend.service.KnockQueryService;
import TtokTtok.Backend.web.dto.KnockRequestDto;
import TtokTtok.Backend.web.dto.KnockResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/knocks")
@RequiredArgsConstructor
public class KnockRestController {

    private final KnockCommandService knockCommandService;
    private final KnockQueryService knockQueryService;

    @PostMapping("/")
    public ApiResponse<KnockResponseDto.CreateResultDto> createKnock(
            @RequestParam("userId") Long userId,
            @RequestBody @Valid KnockRequestDto.CreateDto request) {
        KnockRequest knockRequest = knockCommandService.createKnock(userId, request);
        return ApiResponse.onSuccess(KnockConverter.toCreateResultDto(knockRequest));
    }

    @PostMapping("/{requestId}/responses")
    public ApiResponse<KnockResponseDto.RespondResultDto> addResponse(
            @PathVariable Long requestId,
            @RequestParam("userId") Long userId,
            @RequestBody @Valid KnockRequestDto.RespondDto request) {
        KnockResponse response = knockCommandService.addResponse(requestId, userId, request);
        return ApiResponse.onSuccess(KnockConverter.toRespondResultDto(response));
    }

    @GetMapping("/{requestId}")
    public ApiResponse<KnockResponseDto.KnockResultDto> getKnockResult(
            @PathVariable Long requestId) {
        KnockRequest knockRequest = knockQueryService.findKnockRequestById(requestId);
        return ApiResponse.onSuccess(KnockConverter.toKnockResultDto(knockRequest));
    }

    @PostMapping("/{requestId}/report")
    public ApiResponse<KnockResponseDto.ReportResultDto> reportResult(
            @PathVariable Long requestId,
            @RequestParam("userId") Long userId) { // 요청자 본인 확인을 위해 userId 받음
        KnockReport reported = knockCommandService.reportKnockResult(requestId, userId); // KnockReport 반환
        return ApiResponse.onSuccess(KnockConverter.toReportResultDto(reported)); // DTO 변환
    }

}
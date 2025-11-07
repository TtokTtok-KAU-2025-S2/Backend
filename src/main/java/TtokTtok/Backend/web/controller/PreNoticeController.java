package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.converter.PreNoticeConverter;
import TtokTtok.Backend.domain.PreNotice;
import TtokTtok.Backend.service.PreNoticeService;
import TtokTtok.Backend.web.dto.PreNoticeRequest;
import TtokTtok.Backend.web.dto.PreNoticeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prenotices")
@RequiredArgsConstructor
public class PreNoticeController {

    private final PreNoticeService preNoticeService;

    @PostMapping
    public ApiResponse<PreNoticeResponse.PreNoticeDetailDto> createPreNotice(
            @RequestBody @Valid PreNoticeRequest.CreatePreNoticeDto request) {
        PreNotice preNotice = preNoticeService.createPreNotice(request);
        return ApiResponse.onSuccess(PreNoticeConverter.toPreNoticeDetailDto(preNotice));
    }

    @GetMapping
    public ApiResponse<PreNoticeResponse.PreNoticeListResponse> getPreNoticeList(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "filter", required = false) String filter) { // filter = my_dong
        Pageable pageable = PageRequest.of(page, size);
        Boolean filterByMyDong = "my_dong".equalsIgnoreCase(filter);
        Page<PreNotice> preNoticePage = preNoticeService.getPreNoticeList(pageable, filterByMyDong);
        return ApiResponse.onSuccess(PreNoticeConverter.toPreNoticeListResponse(preNoticePage));
    }

    @GetMapping("/{preNoticeId}")
    public ApiResponse<PreNoticeResponse.PreNoticeDetailDto> getPreNotice(
            @PathVariable Long preNoticeId) {
        PreNotice preNotice = preNoticeService.getPreNotice(preNoticeId);
        return ApiResponse.onSuccess(PreNoticeConverter.toPreNoticeDetailDto(preNotice));
    }

    @PutMapping("/{preNoticeId}")
    public ApiResponse<PreNoticeResponse.PreNoticeDetailDto> updatePreNotice(
            @PathVariable Long preNoticeId,
            @RequestBody @Valid PreNoticeRequest.UpdatePreNoticeDto request) {
        PreNotice preNotice = preNoticeService.updatePreNotice(preNoticeId, request);
        return ApiResponse.onSuccess(PreNoticeConverter.toPreNoticeDetailDto(preNotice));
    }

    @DeleteMapping("/{preNoticeId}")
    public ApiResponse<String> deletePreNotice(
            @PathVariable Long preNoticeId) {
        preNoticeService.deletePreNotice(preNoticeId);
        return ApiResponse.onSuccess("사전 양해 게시글이 성공적으로 삭제되었습니다.");
    }
}













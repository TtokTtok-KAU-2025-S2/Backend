package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse; // ✨ ApiResponse 임포트 필수
import TtokTtok.Backend.converter.NoticeConverter;
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.service.NoticeService;
import TtokTtok.Backend.web.dto.NoticeRequest;
import TtokTtok.Backend.web.dto.NoticeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 1. 공지사항 생성 (ADMIN 권한)
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NoticeResponse.NoticeDetailDto> createNotice(
            @ModelAttribute @Valid NoticeRequest.CreateNoticeDto request
    ) {
        Notice notice = noticeService.createNotice(request);
        // ✨ ResponseEntity 대신 ApiResponse.onSuccess 사용
        return ApiResponse.onSuccess(NoticeConverter.toNoticeDetailDto(notice));
    }

    // 2. 공지사항 상세 조회
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeResponse.NoticeDetailDto> getNotice(@PathVariable Long noticeId) {
        Notice notice = noticeService.getNotice(noticeId);
        // ✨ ApiResponse.onSuccess 사용
        return ApiResponse.onSuccess(NoticeConverter.toNoticeDetailDto(notice));
    }

    // 3. 공지사항 목록 조회
    @GetMapping
    public ApiResponse<NoticeResponse.NoticeListResponse> getNoticeList(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> noticePage = noticeService.getNoticeList(pageable);
        // ✨ ApiResponse.onSuccess 사용
        return ApiResponse.onSuccess(NoticeConverter.toNoticeListResponse(noticePage));
    }
}
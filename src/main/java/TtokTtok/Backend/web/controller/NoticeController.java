package TtokTtok.Backend.web.controller;
//API 엔드포인트 생성

import TtokTtok.Backend.converter.NoticeConverter;
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.service.NoticeService;
import TtokTtok.Backend.web.dto.NoticeRequest;
import TtokTtok.Backend.web.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // PreAuthorize 임포트
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
     private final NoticeService noticeService;

     @PostMapping
     @PreAuthorize("hasRole('ADMIN')") // ADMIN 역할만 접근 허용
     public ResponseEntity<NoticeResponse.NoticeDetailDto> createNotice(@RequestBody NoticeRequest.CreateNoticeDto request) {
         // 이제 서비스 계층에서 현재 로그인한 사용자의 정보를 가져옵니다.
         Notice notice = noticeService.createNotice(request);
         NoticeResponse.NoticeDetailDto responseDto = NoticeConverter.toNoticeDetailDto(notice);
         // 생성된 공지사항의 상세 정보를 응답으로 반환
         return ResponseEntity.ok(responseDto);
     }
 }
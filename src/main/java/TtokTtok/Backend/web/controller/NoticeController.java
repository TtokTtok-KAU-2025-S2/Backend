package TtokTtok.Backend.web.controller;
//API 엔드포인트 생성

import TtokTtok.Backend.converter.NoticeConverter;
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.service.NoticeService;
import TtokTtok.Backend.web.dto.NoticeRequest;
import TtokTtok.Backend.web.dto.NoticeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // PreAuthorize 임포트
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
     private final NoticeService noticeService;
    private final ObjectMapper objectMapper;

     @PostMapping(consumes = "multipart/form-data")
     @PreAuthorize("hasRole('ADMIN')") // ADMIN 역할만 접근 허용
     public ResponseEntity<NoticeResponse.NoticeDetailDto> createNotice(
             // [변경] @RequestPart 대신 @ModelAttribute 사용
             @ModelAttribute @Valid NoticeRequest.CreateNoticeDto request
     ) {
         // [제거] ObjectMapper로 변환하는 로직 삭제
         // NoticeRequest.CreateNoticeDto request = objectMapper.readValue(requestString, NoticeRequest.CreateNoticeDto.class);

         // [변경] 서비스 호출 시 DTO 객체 하나만 전달
         Notice notice = noticeService.createNotice(request);

         NoticeResponse.NoticeDetailDto responseDto = NoticeConverter.toNoticeDetailDto(notice);
         return ResponseEntity.ok(responseDto);
     }

     @GetMapping("/{noticeId}")
     public ResponseEntity<NoticeResponse.NoticeDetailDto> getNotice(@PathVariable Long noticeId) {
         Notice notice = noticeService.getNotice(noticeId);
         NoticeResponse.NoticeDetailDto responseDto = NoticeConverter.toNoticeDetailDto(notice);
         return ResponseEntity.ok(responseDto);
     }
 }
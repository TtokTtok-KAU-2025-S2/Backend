package TtokTtok.Backend.web.controller;
//API 엔드포인트 생성

import TtokTtok.Backend.converter.NoticeConverter;
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.service.NoticeService;
import TtokTtok.Backend.web.dto.NoticeRequest;
import TtokTtok.Backend.web.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // PreAuthorize 임포트
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {
     private final NoticeService noticeService;
    private final ObjectMapper objectMapper;

     @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     @PreAuthorize("hasRole('ADMIN')") // ADMIN 역할만 접근 허용
     public ResponseEntity<NoticeResponse.NoticeDetailDto> createNotice(
             @RequestPart("request") String requestString,
             @RequestPart(value = "image", required = false) MultipartFile image) throws IOException { //이미지 파일 추가 (선택)
         NoticeRequest.CreateNoticeDto request = objectMapper.readValue(requestString, NoticeRequest.CreateNoticeDto.class);

         // 이제 서비스 계층에서 현재 로그인한 사용자의 정보를 가져옵니다.
         Notice notice = noticeService.createNotice(request, image);
         NoticeResponse.NoticeDetailDto responseDto = NoticeConverter.toNoticeDetailDto(notice);
         // 생성된 공지사항의 상세 정보를 응답으로 반환
         return ResponseEntity.ok(responseDto);
     }

     @GetMapping("/{noticeId}")
     public ResponseEntity<NoticeResponse.NoticeDetailDto> getNotice(@PathVariable Long noticeId) {
         Notice notice = noticeService.getNotice(noticeId);
         NoticeResponse.NoticeDetailDto responseDto = NoticeConverter.toNoticeDetailDto(notice);
         return ResponseEntity.ok(responseDto);
     }
 }
package TtokTtok.Backend.service;
//인터페이스 구현

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.config.jwt.SecurityUtil; // SecurityUtil 임포트
import TtokTtok.Backend.converter.NoticeConverter;
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.NoticeRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.NoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

     private final NoticeRepository noticeRepository;
     private final UserRepository userRepository;

     @Override
     @Transactional
     public Notice createNotice(NoticeRequest.CreateNoticeDto request) {
         // 1. 현재 로그인한 사용자의 이메일 가져오기
         String userEmail = SecurityUtil.getCurrentUserEmail();

         // 2. 이메일을 사용해 User 엔티티 조회 (MEMBER_NOT_FOUND 에러 처리)
         User user = userRepository.findByEmail(userEmail)
                 .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

         // 3. NoticeConverter를 사용해 DTO를 Notice 엔티티로 변환
         Notice newNotice = NoticeConverter.toNotice(request, user);

         // 4. NoticeRepository를 사용해 데이터베이스에 저장
         return noticeRepository.save(newNotice);
     }

     @Override
     public Notice getNotice(Long noticeId) {
         return noticeRepository.findById(noticeId)
                 .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));
     }
}
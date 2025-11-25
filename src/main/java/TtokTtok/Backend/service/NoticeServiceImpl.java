package TtokTtok.Backend.service;
//인터페이스 구현

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.aws.s3.AmazonS3Manager;
import TtokTtok.Backend.config.jwt.SecurityUtil; // SecurityUtil 임포트
import TtokTtok.Backend.converter.NoticeConverter;
import TtokTtok.Backend.domain.Notice;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.Uuid;
import TtokTtok.Backend.repository.NoticeRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.repository.UuidRepository;
import TtokTtok.Backend.web.dto.NoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

     private final NoticeRepository noticeRepository;
     private final UserRepository userRepository;
    private final AmazonS3Manager s3Manager; // S3 매니저 주입
    private final UuidRepository uuidRepository; // Uuid 레포지토리 주입

     @Override
     @Transactional
     public Notice createNotice(NoticeRequest.CreateNoticeDto request) {
         // 1. 현재 로그인한 사용자의 이메일 가져오기
         String userEmail = SecurityUtil.getCurrentUserEmail();

         // 2. 이메일을 사용해 User 엔티티 조회 (MEMBER_NOT_FOUND 에러 처리)
         User user = userRepository.findByEmail(userEmail)
                 .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

         String imageUrl = null;
         // 2. [변경] DTO에서 직접 파일을 가져옵니다.
         MultipartFile image = request.getNoticePicture();

         if (image != null && !image.isEmpty()) {
             String uuid = UUID.randomUUID().toString();
             Uuid savedUuid = uuidRepository.save(Uuid.builder()
                     .uuid(uuid).build());

             String keyName = s3Manager.generateNoticeKeyName(savedUuid);
             imageUrl = s3Manager.uploadFile(keyName, image);
         }

         // 3. NoticeConverter를 사용해 DTO를 Notice 엔티티로 변환
         Notice newNotice = NoticeConverter.toNotice(request, user, imageUrl);

         // 4. NoticeRepository를 사용해 데이터베이스에 저장
         return noticeRepository.save(newNotice);
     }

    @Override
    public Notice getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTICE_NOT_FOUND));
    }

    @Override
    public Page<Notice> getNoticeList(Pageable pageable) {
        // 현재 로그인한 유저의 아파트 정보로 필터링
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return noticeRepository.findAllByApartment(user.getApartment(), pageable);
    }
}
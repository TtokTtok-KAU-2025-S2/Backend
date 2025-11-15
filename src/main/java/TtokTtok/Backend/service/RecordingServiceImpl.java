package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.aws.s3.AmazonS3Manager;

import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.Uuid;
import TtokTtok.Backend.domain.VoiceRecording;
import TtokTtok.Backend.repository.RecordingRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.repository.UuidRepository;
import TtokTtok.Backend.web.dto.RecordingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordingServiceImpl implements RecordingService {

    private final UserRepository userRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final RecordingRepository recordingRepository; // [신규]

    @Override
    public RecordingResponse.UploadDto uploadRecording(MultipartFile voiceFile) {

        // 1. [중요] 토큰에서 사용자 이메일(또는 ID) 조회
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 파일 유효성 검사
        if (voiceFile == null || voiceFile.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_IS_EMPTY); // (ErrorStatus에 추가 필요)
        }

        // 3. S3 업로드 (기존 로직 재사용)
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());

        // [신규] 음성 녹음용 S3 키 생성 (S3Manager에 메서드 추가 필요)
        String keyName = s3Manager.generateVoiceMemoKeyName(savedUuid);
        String s3Url = s3Manager.uploadFile(keyName, voiceFile);

        // 4. DB에 메타데이터 저장
        VoiceRecording newRecording = VoiceRecording.builder()
                .user(user) // 토큰으로 조회한 사용자
                .fileUrl(s3Url)
                .originalFileName(voiceFile.getOriginalFilename())
                .build();

        VoiceRecording savedRecording = recordingRepository.save(newRecording);

        // 5. 응답 DTO 반환
        return RecordingResponse.UploadDto.builder()
                .recordingId(savedRecording.getId())
                .fileUrl(s3Url)
                .createdAt(savedRecording.getCreatedAt().toString()) // (DTO 스펙에 맞게 조정)
                .build();
    }
}
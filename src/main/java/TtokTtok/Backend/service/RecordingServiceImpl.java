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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecordingServiceImpl implements RecordingService {

    private final UserRepository userRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final RecordingRepository recordingRepository;

    @Override
    public RecordingResponse.UploadDto uploadRecording(MultipartFile voiceFile, Integer duration, Double dbMax, Double dbAvg) {

        // 1. 사용자 조회
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 파일 유효성 검사
        if (voiceFile == null || voiceFile.isEmpty()) {
            throw new GeneralException(ErrorStatus.FILE_IS_EMPTY);
        }

        // 3. S3 업로드
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
        String keyName = s3Manager.generateVoiceMemoKeyName(savedUuid);
        String s3Url = s3Manager.uploadFile(keyName, voiceFile);

        // 4. [자동 보정] 메타데이터가 없으면 서버에서 임의 값 생성 (테스트/시뮬레이션용)
        if (duration == null || duration <= 0) {
            duration = 10 + ThreadLocalRandom.current().nextInt(50); // 10~60초 랜덤
            log.info("Duration 미입력으로 자동 생성: {}초", duration);
        }

        if (dbMax == null) {
            dbMax = 60.0 + ThreadLocalRandom.current().nextDouble() * 20.0; // 60~80dB 랜덤
        }

        if (dbAvg == null) {
            dbAvg = dbMax - (5.0 + ThreadLocalRandom.current().nextDouble() * 10.0); // Max보다 5~15dB 낮게
        }

        // 5. DB 저장
        VoiceRecording newRecording = VoiceRecording.builder()
                .user(user)
                .fileUrl(s3Url)
                .originalFileName(voiceFile.getOriginalFilename())
                .duration(duration)
                .dbMax(Math.round(dbMax * 10.0) / 10.0) // 소수점 첫째자리 반올림
                .dbAvg(Math.round(dbAvg * 10.0) / 10.0)
                .build();

        VoiceRecording savedRecording = recordingRepository.save(newRecording);

        // 6. 응답 반환
        LocalDateTime createdTime = savedRecording.getCreatedAt() != null ?
                savedRecording.getCreatedAt() : LocalDateTime.now();
        return RecordingResponse.UploadDto.builder()
                .recordingId(savedRecording.getId())
                .fileUrl(s3Url)
                .createdAt(createdTime.toString())
                .build();
    }
}
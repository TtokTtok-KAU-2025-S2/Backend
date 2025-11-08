package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.web.dto.noise.NoiseRecordUpdateDTO;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.service.GeminiService;
import TtokTtok.Backend.web.dto.noise.NoiseRecordCreateDTO;
import TtokTtok.Backend.web.dto.noise.NoiseRecordDTO;
import TtokTtok.Backend.config.jwt.SecurityUtil; // ⭐ SecurityUtil import 추가
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor
@Service
@Transactional
public class NoiseRecordService {

    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    // 총 소음 기록 수 조회
    public long getTotalCount() { // ⭐ userId 매개변수 제거
        Long userId = SecurityUtil.getCurrentUserId(); // JWT에서 추출
        User user = getUser(userId);
        return noiseDiaryRepository.countByUser(user);
    }

    // 특정 달 소음 기록 수 조회
    public long getMonthlyCount(int year, int month) { // ⭐ userId 매개변수 제거
        Long userId = SecurityUtil.getCurrentUserId(); // JWT에서 추출
        User user = getUser(userId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.withDayOfMonth(startDate.lengthOfMonth()).atTime(LocalTime.MAX);

        return noiseDiaryRepository.countByUserAndCreatedAtBetween(user, start, end);
    }

    //전체 평균 dB 조회
    public double getAverageDb() { // ⭐ userId 매개변수 제거
        Long userId = SecurityUtil.getCurrentUserId(); // JWT에서 추출
        User user = getUser(userId);
        Double avg = noiseDiaryRepository.findAverageDbByUser(user);
        return avg != null ? avg : 0.0;
    }

    // 사용자 조회 헬퍼
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 소음 기록 한 건 업데이트
    public NoiseRecordDTO updateNoiseRecord(Long recordId, NoiseRecordUpdateDTO request) { // ⭐ userId 매개변수 제거
        Long userId = SecurityUtil.getCurrentUserId(); // JWT에서 추출
        User user = getUser(userId);

        NoiseDiary diary = noiseDiaryRepository.findByIdAndUser(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 소음 기록이 존재하지 않습니다."));

        // 4. enum 변환 (카테고리 / 등급)
        try {
            diary.setCategory(request.getCategory());
            diary.setGrade(request.getNoiseGrade());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("입력값이 유효하지 않습니다. (카테고리 또는 등급 형식 오류)");
        }

        // 5. occuredAt 파싱
        diary.setOccuredAt(request.getOccuredAt());

        // 6. 수치 & 메모 업데이트
        diary.setDbHigh(request.getDbHigh());
        diary.setDbAvg(request.getDbAvg());
        diary.setSummary(request.getSummary());
        diary.setUpdateAt(LocalDateTime.now());

        // 7. 응답 DTO로 변환
        return new NoiseRecordDTO(
                diary.getId(),
                diary.getCategory(),
                diary.getOccuredAt() ,
                diary.getGrade(),
                diary.getDbHigh(),
                diary.getDbAvg(),
                diary.getSummary(),
                diary.getUpdateAt()
        );
    }


    @Transactional
    public void deleteNoiseRecord(Long recordId) {
        Long userId = SecurityUtil.getCurrentUserId(); // 이미 토큰에서 추출됨

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER4001"));

        NoiseDiary diary = noiseDiaryRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("NOISE4006"));

        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("NOISE4008");
        }

        noiseDiaryRepository.delete(diary); // 하드 딜리트
    }
}
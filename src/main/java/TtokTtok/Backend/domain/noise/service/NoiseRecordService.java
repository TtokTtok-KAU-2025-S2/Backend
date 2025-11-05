package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.noise.dto.NoiseRecordDTO;
import TtokTtok.Backend.domain.noise.dto.NoiseRecordUpdateDTO;
import TtokTtok.Backend.domain.noise.repository.NoiseDiaryRepository;
import TtokTtok.Backend.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class NoiseRecordService {

    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;

    public NoiseRecordService(NoiseDiaryRepository noiseLogRepository, UserRepository userRepository) {
        this.noiseDiaryRepository = noiseLogRepository;
        this.userRepository = userRepository;
    }

    // 총 소음 기록 수 조회
    public long getTotalCount(Long userId) {
        User user = getUser(userId);
        return noiseDiaryRepository.countByUserAndDeletedFalse(user);
    }

    // 특정 달 소음 기록 수 조회
    public long getMonthlyCount(Long userId, int year, int month) {
        User user = getUser(userId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = startDate.withDayOfMonth(startDate.lengthOfMonth()).atTime(LocalTime.MAX);

        return noiseDiaryRepository.countByUserAndCreatedAtBetweenAndDeletedFalse(user, start, end);
    }

    //전체 평균 dB 조회
    public double getAverageDb(Long userId) {
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
    public NoiseRecordDTO updateNoiseRecord(Long userId, Long recordId, NoiseRecordUpdateDTO request) {

        // 1. 회원 검증
        User user = getUser(userId);

        // 2. 기록 조회
        NoiseDiary diary = noiseDiaryRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 소음 기록이 존재하지 않습니다."));

        // 3. 소유자 확인
        if (!diary.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정하려는 소음 기록이 존재하지 않습니다.");
        }

        // 4. enum 변환 (카테고리 / 등급)
        try {
            diary.setCategory(NoiseCategory.valueOf(request.getCategory()));     // FOOTSTEPS, MUSIC ...
            diary.setGrade(NoiseGrade.valueOf(request.getNoiseGrade()));        // QUIET, NORMAL, LOUD
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("입력값이 유효하지 않습니다. (카테고리 또는 등급 형식 오류)");
        }

        // 5. occuredAt 파싱 (예: 2025-10-27T13:30:00)
        try {
            diary.setOccuredAt(request.getCreatedAt());
        } catch (Exception e) {
            throw new IllegalArgumentException("입력값이 유효하지 않습니다. (날짜/시간 형식 오류)");
        }

        // 6. 수치 & 메모 업데이트
        diary.setDbHigh(request.getDbHigh());
        diary.setDbAvg(request.getDbAvg());
        diary.setSummary(request.getSummary());
        diary.setUpdateAt(LocalDateTime.now());

        // 7. 응답 DTO로 변환 (NoiseRecordDTO 구조에 맞게)
        return new NoiseRecordDTO(
                diary.getId(),
                diary.getCategory().name(),
                diary.getOccuredAt() != null ? diary.getOccuredAt().toString() : null,
                diary.getGrade().name(),
                diary.getDbHigh().doubleValue(),
                diary.getDbAvg().doubleValue(),
                diary.getSummary(),
                diary.getUpdateAt() != null ? diary.getUpdateAt().toString() : null
        );
    }

    @Transactional
    public void softDeleteNoiseRecord(Long userId, Long recordId) {
        User user = getUser(userId); // MEMBER4001 던지는 기존 유저 조회 메서드

        NoiseDiary record = noiseDiaryRepository
                .findByIdAndUserAndDeletedFalse(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("NOISE4006"));

        record.setDeleted(true); // 엔티티에 boolean deleted 필드 있어야 함
    }


}

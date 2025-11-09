package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.Vote;
import TtokTtok.Backend.repository.VoteRepository;
import TtokTtok.Backend.web.dto.NoiseDiaryRequestDTO;
import TtokTtok.Backend.web.dto.NoiseDiaryResponseDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static TtokTtok.Backend.common.enums.NoiseCategory.*;

@RequiredArgsConstructor
@Service
@Transactional
public class NoiseRecordService {

    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final VoteRepository voteRepository;

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


    // ===========================
    // 소음 기록 생성 (Create)
    // ===========================
    @Transactional
    public NoiseDiaryResponseDTO createNoiseDiary(NoiseDiaryRequestDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = getUser(userId);

        // 1. 녹음이 없는 경우 임시 dB 값 설정
        if (request.getDbAvg() == null || request.getDbHigh() == null) {
            BigDecimal avg;
            switch (request.getCategory()) {
                case FOOTSTEPS -> avg = BigDecimal.valueOf(45.0);
                case HAMMERING -> avg = BigDecimal.valueOf(75.0);
                case FURNITURE -> avg = BigDecimal.valueOf(55.0);
                case MUSIC -> avg = BigDecimal.valueOf(60.0);
                default -> avg = BigDecimal.valueOf(50.0);
            }
            request.setDbAvg(avg);
            request.setDbHigh(avg.add(BigDecimal.TEN));
        }

        // 2. 엔티티 생성
        LocalDateTime now = LocalDateTime.now();
        NoiseDiary diary = NoiseDiary.builder()
                .user(user)
                .duration(request.getDuration())
                .dbAvg(request.getDbAvg())
                .dbHigh(request.getDbHigh())
                .category(request.getCategory())
                .grade(request.getGrade())
                .description(request.getDescription())
                .summary(null)          // AI 기능 미구현
                .reportYn(false)        // 기본 false
                .occuredAt(now)         // 소음 발생 시각
                .updateAt(now)          // 최초 생성 시 updateAt도 now로 세팅
                .build();

        noiseDiaryRepository.save(diary);

        // 3. ResponseDTO로 변환
        return NoiseDiaryResponseDTO.builder()
                .id(diary.getId())
                .userId(userId)
                .duration(diary.getDuration())
                .dbAvg(diary.getDbAvg())
                .dbHigh(diary.getDbHigh())
                .category(diary.getCategory())
                .grade(diary.getGrade())
                .description(diary.getDescription())
                .summary(diary.getSummary())
                .occuredAt(diary.getOccuredAt())
                .updateAt(diary.getUpdateAt())
                .build();
    }




    /**
     * 소음 기록 전송 (reportYn = true, Vote 생성)
     */
    @Transactional
    public void sendNoiseDiary(Long recordId) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        NoiseDiary diary = noiseDiaryRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("전송할 소음 기록이 존재하지 않습니다."));

        // 본인 기록인지 확인
        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 기록만 전송 가능합니다.");
        }

        // reportYn, reportedAt 업데이트
        diary.setReportYn(true);
        diary.setReportedAt(LocalDateTime.now());

        // Vote 객체 생성
        Vote vote = Vote.builder()
                .user(user)
                .noiseDiary(diary)
                .type(null)
                .build();

// repository 인스턴스로 저장
        voteRepository.save(vote);

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
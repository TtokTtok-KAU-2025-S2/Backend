package TtokTtok.Backend.service;

import TtokTtok.Backend.common.enums.VoteType;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.Vote;
import TtokTtok.Backend.repository.VoteRepository;
import TtokTtok.Backend.web.dto.NoiseDiaryRequestDTO;
import TtokTtok.Backend.web.dto.NoiseDiaryResponseDTO;
import TtokTtok.Backend.web.dto.noise.NoiseRecordUpdateDTO;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.noise.NoiseRecordDTO;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NoiseRecordService {

    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final AnalysisService analysisService;

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
                // ✨ [추가된 항목들에 대한 기본값 설정]
                case VOICE -> avg = BigDecimal.valueOf(55.0);        // 대화/고성방가
                case PET -> avg = BigDecimal.valueOf(65.0);          // 개 짖는 소리는 꽤 큼
                case APPLIANCE -> avg = BigDecimal.valueOf(60.0);    // 청소기/세탁기
                case DOOR -> avg = BigDecimal.valueOf(65.0);         // 문 쾅 닫는 소리 (순간 소음)
                case WATER -> avg = BigDecimal.valueOf(50.0);        // 물 소리
                case CONSTRUCTION -> avg = BigDecimal.valueOf(75.0); // 공사 소음 (매우 시끄러움)
                case EXERCISE -> avg = BigDecimal.valueOf(60.0);     // 런닝머신/덤벨

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

        // ----------------------------------------
        // ✨ 1. AI 요약 생성 및 저장 로직 추가
        // ----------------------------------------
        if (diary.getSummary() == null || diary.getSummary().isEmpty()) {
            try {
                log.info("소음 일기 전송 시 AI 요약 생성 시작 (ID: {})", recordId);
                String aiSummary = analysisService.generateSummaryForReport(diary);
                if (aiSummary != null) {
                    diary.setSummary(aiSummary);
                    log.info("AI 요약 생성 성공: {}", aiSummary);
                }
            } catch (Exception e) {
                log.error("AI 요약 생성 실패 (무시하고 전송 진행): {}", e.getMessage());
            }
        }

        // 2. reportYn, reportedAt 업데이트
        diary.setReportYn(true);
        diary.setReportedAt(LocalDateTime.now());

        // 3. Vote 객체 생성 (내가 작성한 글에는 '안들려요' 등 투표 안 함? 요구사항에 따라 조정 가능. 여기선 유지)
        // *참고: 작성자 본인이 '안 들려요'를 누르는 것은 이상할 수 있으나, 기존 로직 유지
        Vote vote = Vote.builder()
                .user(user)
                .noiseDiary(diary)
                .type(null)
                .build();

        voteRepository.save(vote);
    }


    // ------------------------------------------------------------
    // ✨ [수정] 소음 기록 수정 로직 (description 반영, summary 제거)
    // ------------------------------------------------------------
    public NoiseRecordDTO updateNoiseRecord(Long recordId, NoiseRecordUpdateDTO request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = getUser(userId);

        NoiseDiary diary = noiseDiaryRepository.findByIdAndUser(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("수정하려는 소음 기록이 존재하지 않습니다."));

        try {
            diary.setCategory(request.getCategory());
            diary.setGrade(request.getNoiseGrade());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("입력값이 유효하지 않습니다. (카테고리 또는 등급 형식 오류)");
        }

        diary.setOccuredAt(request.getOccuredAt());
        diary.setDbHigh(request.getDbHigh());
        diary.setDbAvg(request.getDbAvg());

        // ✨ [수정] description(사용자 메모)만 업데이트
        diary.setDescription(request.getDescription());

        // summary 업데이트 로직은 제거 (요청 DTO에서 필드를 삭제했으므로)

        diary.setUpdateAt(LocalDateTime.now());

        // 응답 DTO 생성 (NoiseRecordDTO에도 summary가 있다면 null로 처리하거나 DTO를 수정해야 함)
        // 여기서는 DTO 구조상 summary 필드가 있다면 기존 값을 넣거나 null 처리
        return new NoiseRecordDTO(
                userId,
                diary.getId(),
                diary.getCategory(),
                diary.getOccuredAt(),
                diary.getGrade(),
                diary.getDbHigh(),
                diary.getDbAvg(),
                diary.getDescription(), // ✨ description 반영
                diary.getUpdateAt(),
                diary.getDuration()
        );
    }


    @Transactional
    public void deleteNoiseRecord(Long recordId) {
        Long userId = SecurityUtil.getCurrentUserId(); // 이미 토큰에서 추출됨

        NoiseDiary diary = noiseDiaryRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("NOISE4006"));

        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("NOISE4008");
        }

        noiseDiaryRepository.delete(diary); // 하드 딜리트
    }
}
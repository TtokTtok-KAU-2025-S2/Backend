package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.noise.dto.NoiseRecordCreateDTO;
import TtokTtok.Backend.domain.noise.dto.NoiseRecordDTO;
import TtokTtok.Backend.domain.noise.dto.NoiseRecordUpdateDTO;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.service.FileStorageService;
import TtokTtok.Backend.service.GeminiService;
import TtokTtok.Backend.web.dto.AnalysisRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor // 👈 모든 final 필드를 포함하는 생성자를 자동으로 만들고 @Autowired 역할을 수행합니다.
@Service
@Transactional
public class NoiseRecordService {

    // 모든 의존성 필드를 final로 선언하여 주입 대상임을 명시
    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final GeminiService geminiService;

    // 🚨 명시적으로 정의했던 생성자(오류의 원인)는 제거되었습니다.

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

        User user = getUser(userId);

        NoiseDiary diary = noiseDiaryRepository.findByIdAndUserAndDeletedFalse(recordId, user)
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
    public void softDeleteNoiseRecord(Long userId, Long recordId) {
        User user = getUser(userId);

        NoiseDiary record = noiseDiaryRepository
                .findByIdAndUserAndDeletedFalse(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("NOISE4006"));

        record.setDeleted(true);
    }

    // --- AI 생성 및 최종 저장 기능 (1, 2, 3, 4, 5단계) ---

    // 3, 4단계: AI 소음 분석 텍스트 생성/재생성
    public String generateNoiseAnalysis(MultipartFile audioFile, AnalysisRequestDTO request) {
        return geminiService.generateNoiseAnalysis(
                audioFile,
                request.getCategory(),
                request.getDescription(),
                request.getDbAvg()
        );
    }


    // 5단계: 일기 최종 저장 (DB 반영)
    @Transactional
    public Long createNoiseRecord(NoiseRecordCreateDTO createRequest, MultipartFile audioFile) {

        // 1. 오디오 파일 저장
        String audioFilePath = fileStorageService.saveAudioFile(audioFile);

        // 2. User 객체 조회 (DB 무결성을 위해)
        User user = getUser(createRequest.getUserId());

        // 3. DTO를 Entity로 변환 및 필수 필드 초기화
        NoiseDiary noiseDiary = new NoiseDiary();

        // 필드 매핑
        noiseDiary.setUser(user);
        noiseDiary.setDuration(createRequest.getDuration());
        noiseDiary.setGrade(createRequest.getGrade());
        noiseDiary.setDbHigh(createRequest.getDbHigh());
        noiseDiary.setDbAvg(createRequest.getDbAvg());
        noiseDiary.setOccuredAt(createRequest.getOccuredAt());
        noiseDiary.setCategory(createRequest.getCategory());
        noiseDiary.setDescription(createRequest.getDescription());
        noiseDiary.setSummary(createRequest.getSummary());
        noiseDiary.setAudioFilePath(audioFilePath);

        // 시스템/기본값 설정
        noiseDiary.setDeleted(false);
        noiseDiary.setReportYn(false);

        // 4. 데이터 저장 (DB 반영)
        NoiseDiary savedRecord = noiseDiaryRepository.save(noiseDiary);

        return savedRecord.getId();
    }


    /**
     * ⚠️ 소음 기록을 현황판에 게시(전송) 처리합니다. (핵심 기능)
     * @param userId 요청 사용자 ID
     * @param recordId 현황판에 전송할 소음 기록 ID
     * @return 현황판에 게시된 기록 ID
     */
    @Transactional
    public Long reportNoiseRecord(Long userId, Long recordId) {

        // 1. 회원 검증 및 조회 (Helper 메서드 사용)
        User user = getUser(userId);

        // 2. 기록 조회 및 소유자 검증 (삭제되지 않은 기록만 조회)
        NoiseDiary record = noiseDiaryRepository
                .findByIdAndUserAndDeletedFalse(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("NOISE4006: 해당 소음 기록을 찾을 수 없습니다."));

        // 3. 이미 현황판에 등록되었는지 확인
        if (record.getReportYn()) {
            throw new IllegalArgumentException("NOISE4007: 이미 현황판에 게시된 기록입니다.");
        }

        // 4. 현황판 게시 상태 업데이트
        record.setReportYn(true);
        record.setReportedAt(LocalDateTime.now()); // 게시 시각 기록

        // 5. DB 저장 (Transactional에 의해 자동 반영되지만, 명시적으로 save 가능)
        noiseDiaryRepository.save(record);

        return record.getId();
    }
}
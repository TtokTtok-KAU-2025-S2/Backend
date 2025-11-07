package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.noise.dto.DailyNoiseDiaryDTO;
import TtokTtok.Backend.domain.noise.dto.NoiseMonthlyCalendarDTO;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class NoiseCalendarService {

    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;

    /**
     * 1) 월간 캘린더 (날짜 + hasNoiseLog 여부)
     */
    public NoiseMonthlyCalendarDTO getMonthlyNoiseCalendar(Long userId, int year, int month) {
        validateUserAndDate(userId, year, month);

        // 이번 달의 모든 날짜 생성
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 이번 달의 소음 기록 날짜 조회
        List<LocalDate> recordDates =
                noiseDiaryRepository.findAllDatesByUserAndMonth(userId, startDate, endDate);

        // 날짜별 hasNoiseLog 여부 생성
        List<NoiseMonthlyCalendarDTO.DateInfo> data = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> {
                    LocalDate currentDate = LocalDate.of(year, month, day);
                    boolean hasNoiseLog = recordDates.contains(currentDate);
                    return new NoiseMonthlyCalendarDTO.DateInfo(
                            currentDate.toString(),
                            hasNoiseLog
                    );
                })
                .collect(Collectors.toList());

        return new NoiseMonthlyCalendarDTO(userId, year, month, data);
    }


    /**
     * 2) 날짜별 소음 일기 조회
     * - 하루(date)에 해당하는 소음 기록 목록 반환
     */
    public DailyNoiseDiaryDTO getDailyNoiseDiary(Long userId, LocalDate date) {
        validateUserAndDate(userId, date.getYear(), date.getMonthValue());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // [date 00:00, date+1 00:00) 구간
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.plusDays(1).atStartOfDay();

        // 하루 동안의 일기 (생성 시간 기준 오름차순, soft delete 제외)
        List<NoiseDiary> diaries = noiseDiaryRepository
                .findByUserAndCreatedAtBetweenOrderByCreatedAtAsc(
                        user,
                        startDateTime,
                        endDateTime
                );

        // 엔티티 → DTO 매핑
        List<DailyNoiseDiaryDTO.NoiseRecord> records = diaries.stream()
                .map(diary -> new DailyNoiseDiaryDTO.NoiseRecord(
                        diary.getId(),                      // recordId
                        diary.getCategory(),        // category (String)
                        diary.getOccuredAt(),              // occuredAt (LocalDateTime)
                        diary.getUpdateAt(),               // updatedAt (LocalDateTime)
                        diary.getGrade(),           // grade (String)
                        diary.getDbHigh(),                 // dbHigh
                        diary.getDbAvg(),                  // dbAvg
                        diary.getSummary()                 // summary
                ))
                .collect(Collectors.toList());

        // DailyNoiseDiaryDTO(userId, year, month, records)
        return new DailyNoiseDiaryDTO(
                userId,
                date.getYear(),
                date.getMonthValue(),
                records
        );
    }

    // 사용자 ID, 연도, 월의 유효성을 검사
    private void validateUserAndDate(Long userId, int year, int month) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        if (year < 2000 || month < 1 || month > 12) {
            throw new IllegalArgumentException("올바르지 않은 연도 또는 월 형식입니다.");
        }
    }

}
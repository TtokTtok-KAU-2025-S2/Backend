package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.noise.dto.NoiseCalendarDTO;

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
     * 월간 캘린더 (날짜 + hasNoiseLog 여부)
     */
    public NoiseCalendarDTO getMonthlyNoiseCalendar(Long userId, int year, int month) {
        validateUserAndDate(userId, year, month);

        // 이번 달의 모든 날짜 생성
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 이번 달의 소음 기록 날짜 조회
        List<LocalDate> recordDates = noiseDiaryRepository.findAllDatesByUserAndMonth(userId, startDate, endDate);

        // 날짜별 hasNoiseLog 여부 생성
        List<NoiseCalendarDTO.DateInfo> data = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> {
                    LocalDate currentDate = LocalDate.of(year, month, day);
                    boolean hasNoiseLog = recordDates.contains(currentDate);
                    return new NoiseCalendarDTO.DateInfo(currentDate.toString(), hasNoiseLog);
                })
                .collect(Collectors.toList());

        return new NoiseCalendarDTO(userId, year, month, data);

    }


    /**
     * 월간 캘린더 + 날짜별 소음 일기 상세
     *
     * - 각 날짜의 noiseList는 createdAt 기준으로 시간순 정렬
     * - 프론트에서:
     *   - 특정 날짜를 클릭하면 해당 날짜의 noiseList만 사용하면 되고,
     *   - 날짜 선택 해제(default) 시에는 한 달치 모든 noiseList를 합쳐서
     *     "해당 월 전체를 시간순 정렬한 목록"으로 사용할 수 있음
     */

    public NoiseCalendarDTO getMonthlyNoiseCalendarDetails(Long userId, int year, int month) {
        validateUserAndDate(userId, year, month);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 시작일: 해당 월 1일 00:00:00 (포함)
        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        // 종료일: 다음 달 1일 00:00:00 (미포함)
        LocalDateTime endDateTime = yearMonth.plusMonths(1).atDay(1).atStartOfDay();


        // 한 달간 모든 일기 조회 (생성 시간 기준 오름차순)
        List<NoiseDiary> diaries = noiseDiaryRepository.findByUserAndCreatedAtBetweenAndDeletedFalseOrderByCreatedAtAsc(
                user,
                startDateTime,
                endDateTime
        );

        List<NoiseCalendarDTO.DateInfo> data = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .mapToObj(day -> {
                    LocalDate currentDate = LocalDate.of(year, month, day);

                    // 해당 날짜 일기 필터링
                    List<NoiseDiary> dailyLogs = diaries.stream()
                            .filter(diary -> diary.getCreatedAt().toLocalDate().equals(currentDate))
                            .collect(Collectors.toList());

                    boolean hasNoiseLog = !dailyLogs.isEmpty();

                    List<NoiseCalendarDTO.NoiseLogInfo> noiseList = dailyLogs.stream()
                            .map(diary -> new NoiseCalendarDTO.NoiseLogInfo(
                                    diary.getCategory().name(),
                                    diary.getOccuredAt(),
                                    diary.getUpdateAt(),
                                    diary.getGrade().name(),
                                    diary.getDbAvg(),
                                    diary.getDbHigh(),
                                    diary.getSummary()
                            ))
                            .collect(Collectors.toList());

                    return new NoiseCalendarDTO.DateInfo(
                            currentDate.toString(),
                            hasNoiseLog,
                            noiseList
                    );
                })
                .collect(Collectors.toList());

        return new NoiseCalendarDTO(userId, year, month, data);
    }

    //사용자 ID, 연도, 월의 유효성을 검사
    private void validateUserAndDate(Long userId, int year, int month) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        if (year < 2000 || month < 1 || month > 12) {
            throw new IllegalArgumentException("올바르지 않은 연도 또는 월 형식입니다.");
        }
    }

}

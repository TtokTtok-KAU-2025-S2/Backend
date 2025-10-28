package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.domain.noise.repository.NoiseLogRepository;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class NoiseAnalysisService {
    private final NoiseLogRepository noiseLogRepository;
    private final UserRepository userRepository;

    public NoiseAnalysisService(NoiseLogRepository noiseLogRepository, UserRepository userRepository) {
        this.noiseLogRepository = noiseLogRepository;
        this.userRepository = userRepository;
    }


    public Long getMonthlyNoiseCount(Long userId, Integer year, Integer month) {
        //사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        //월의 시작일과 끝일 계산
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.of(year, month);
        } catch (java.time.DateTimeException e) {
            // 이 예외는 Controller의 범용 Exception 핸들러가 처리합니다.
            throw new IllegalArgumentException("올바르지 않은 연도 또는 월 형식입니다.");
        }

        //월의 첫 날의 시작 시간(00:00:00)
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        //월의 마지막 날의 끝 시간
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);

        //Repository를 통해 기록 개수 조회
        return noiseLogRepository.countByUserAndLogDatetimeBetween(user, startOfMonth, endOfMonth);
    }



    public Double getMonthlyAverageIntensity(Long userId, Integer year, Integer month) {
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 월의 시작일과 끝일 계산
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.of(year, month);
        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException("올바르지 않은 연도 또는 월 형식입니다.");
        }

        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999);

        // Repository를 통해 평균 강도 조회
        // 기록이 없을 경우 AVG()는 NULL을 반환하며, Optional.orElse(0.0)이 이를 처리
        Double average = noiseLogRepository.findAverageNoiseLevelByUserAndLogDatetimeBetween(
                user, startOfMonth, endOfMonth
        ).orElse(0.0);

        // 소수점 첫째 자리까지만 표시되도록 반올림 및 포맷팅
        return Math.round(average * 10.0) / 10.0;
    }
}

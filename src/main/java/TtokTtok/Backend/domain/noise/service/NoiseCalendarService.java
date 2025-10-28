package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.domain.noise.dto.NoiseCalendarDTO;
import TtokTtok.Backend.domain.noise.entity.NoiseLog;
import TtokTtok.Backend.domain.noise.repository.NoiseLogRepository;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class NoiseCalendarService {
    private final NoiseLogRepository noiseLogRepository;
    private final UserRepository userRepository;

    public NoiseCalendarService(NoiseLogRepository noiseLogRepository, UserRepository userRepository) {
        this.noiseLogRepository = noiseLogRepository;
        this.userRepository = userRepository;
    }

    public List<NoiseCalendarDTO> getMonthlyCalendar(Long userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<NoiseLog> records = noiseLogRepository.findByUserAndLogDatetimeBetweenOrderByLogDatetimeDesc(user, startDateTime, endDateTime);

        int days = startDate.lengthOfMonth();
        List<NoiseCalendarDTO> calendar = IntStream.rangeClosed(1, days)
                .mapToObj(day -> {
                    LocalDate date = LocalDate.of(year, month, day);
                    boolean hasNoiseLog = records.stream()
                            .anyMatch(r -> r.getLogDatetime().toLocalDate().equals(date));
                    return new NoiseCalendarDTO(date, hasNoiseLog);
                })
                .toList();

        return calendar;
    }
}

package TtokTtok.Backend.domain.noise.service;

import TtokTtok.Backend.domain.noise.dto.NoiseRecordDTO;
import TtokTtok.Backend.domain.noise.entity.NoiseLog;
import TtokTtok.Backend.domain.noise.repository.NoiseLogRepository;
import TtokTtok.Backend.domain.user.User;
import TtokTtok.Backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoiseRecordService {

    private final NoiseLogRepository noiseLogRepository;
    private final UserRepository userRepository;

    public NoiseRecordService(NoiseLogRepository noiseLogRepository, UserRepository userRepository) {
        this.noiseLogRepository = noiseLogRepository;
        this.userRepository = userRepository;
    }

    private NoiseRecordDTO mapToDTO(NoiseLog log){
        return new NoiseRecordDTO(
                log.getId(),
                log.getLogDatetime().toLocalDate(),
                log.getLogDatetime().toLocalTime(),
                log.getNoiseType(),
                log.getNoiseLevel(),
                log.getMemo(),
                log.getCreatedAt(),
                log.getModifiedAt()
        );
    }

    public List<NoiseRecordDTO> getRecordsByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<NoiseLog> logs = noiseLogRepository.findByUserAndLogDatetimeBetweenOrderByLogDatetimeDesc(user, start, end);

        return logs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public NoiseRecordDTO updateRecord(Long recordId, NoiseRecordDTO dto) {
        NoiseLog log = noiseLogRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소음 기록입니다."));

        log.setNoiseType(dto.getNoiseType());
        log.setNoiseLevel(dto.getNoiseLevel());
        log.setMemo(dto.getMemo());

        NoiseLog updated = noiseLogRepository.save(log);

        return mapToDTO(updated);
    }


    public NoiseRecordDTO createRecord(Long userId, NoiseRecordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDateTime logDatetime = LocalDateTime.of(dto.getLogDate(), dto.getLogTime());

        NoiseLog log = new NoiseLog();
        log.setUser(user);
        log.setLogDatetime(logDatetime);
        log.setNoiseType(dto.getNoiseType());
        log.setNoiseLevel(dto.getNoiseLevel());
        log.setMemo(dto.getMemo());

        NoiseLog saved = noiseLogRepository.save(log);

        return mapToDTO(saved);
    }

    public NoiseRecordDTO deleteRecord(Long recordId) {
        NoiseLog log = noiseLogRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소음 기록입니다."));

        NoiseRecordDTO deletedDTO = mapToDTO(log);

        noiseLogRepository.delete(log);

        return deletedDTO;

    }

}

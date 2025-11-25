package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.VoiceRecording;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordingRepository extends JpaRepository<VoiceRecording, Long> {
    // 사용자의 가장 최근 녹음 파일 1건 조회
    Optional<VoiceRecording> findTopByUserOrderByCreatedAtDesc(User user);
}
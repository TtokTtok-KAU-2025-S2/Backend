package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.VoiceRecording;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordingRepository extends JpaRepository<VoiceRecording, Long> {
}
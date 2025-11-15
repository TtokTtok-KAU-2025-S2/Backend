package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}

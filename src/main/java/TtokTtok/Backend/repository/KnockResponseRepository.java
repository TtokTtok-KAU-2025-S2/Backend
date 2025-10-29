package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.knock.KnockResponse;
import TtokTtok.Backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnockResponseRepository extends JpaRepository<KnockResponse, Long> {
    boolean existsByRequestAndResponder(KnockRequest request, User responder);
}

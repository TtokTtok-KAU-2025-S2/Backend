package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface KnockRequestRepository extends JpaRepository<KnockRequest, Long>{
    // 24시간이 지난 활성화된(isActive=true) 요청을 찾는 쿼리
    List<KnockRequest> findByIsActiveTrueAndRequestTimeBefore(LocalDateTime expirationTime);

    // 사용자가 현재 활성화된 요청을 가지고 있는지 확인하는 메서드
    boolean existsByRequesterAndIsActiveTrue(User requester);
}

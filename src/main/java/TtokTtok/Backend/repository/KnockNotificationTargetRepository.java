package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.knock.KnockNotificationTarget;
import TtokTtok.Backend.domain.knock.KnockRequest;
import TtokTtok.Backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KnockNotificationTargetRepository extends JpaRepository<KnockNotificationTarget, Long> {

    /**
     * 특정 '똑똑' 요청에 대해 특정 사용자가 알림 대상이었는지 조회합니다.
     * @param knockRequest 조회할 '똑똑' 요청 엔티티
     * @param targetUser 조회할 사용자 엔티티
     * @return Optional<KnockNotificationTarget>
     */
    Optional<KnockNotificationTarget> findByKnockRequestAndTargetUser(KnockRequest knockRequest, User targetUser);

    /**
     * 특정 '똑똑' 요청에 대해 응답하지 않은 대상자 목록을 조회합니다. (responded = false)
     * @param knockRequest 조회할 '똑똑' 요청 엔티티
     * @return List<KnockNotificationTarget>
     */
    List<KnockNotificationTarget> findByKnockRequestAndRespondedFalse(KnockRequest knockRequest);
}
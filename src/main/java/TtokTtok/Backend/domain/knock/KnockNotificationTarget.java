package TtokTtok.Backend.domain.knock;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "KNOCK_NOTIFICATION_TARGET")
@Getter
@Setter
@NoArgsConstructor
public class KnockNotificationTarget extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private KnockRequest knockRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User targetUser;

    @Column(name = "responded", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean responded = false;

    public KnockNotificationTarget(KnockRequest knockRequest, User targetUser) {
        this.knockRequest = knockRequest;
        this.targetUser = targetUser;
        this.responded = false;
        // 양방향 연관관계 설정
        knockRequest.getNotificationTargets().add(this);
        targetUser.getKnockNotificationTargets().add(this);
    }
}
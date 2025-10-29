package TtokTtok.Backend.domain.user;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.common.enums.UserRole;
import TtokTtok.Backend.domain.complex.AptUnit;
import TtokTtok.Backend.domain.knock.KnockNotificationTarget;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
@Getter @Setter
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apt_id")
    private AptUnit aptUnit;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "trust_index", nullable = false)
    private Double trustIndex = 60.0;

    // --- 신규 필드 ---
    @Column(name = "missed_knock_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer missedKnockCount = 0; // '똑똑' 미응답 횟수 (기본값 0)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.RESIDENT; // 사용자 역할 (입주민/관리자, 기본

    @OneToMany(mappedBy = "user")
    private List<TtokTtok.Backend.domain.community.Article> articles;

    @OneToMany(mappedBy = "user")
    private List<TtokTtok.Backend.domain.community.Reaction> reactions;

    @OneToMany(mappedBy = "user")
    private List<TtokTtok.Backend.domain.noise.NoiseLog> noiseLogs;

    @OneToMany(mappedBy = "user")
    private List<PointsLog> pointsLogs;

    @OneToMany(mappedBy = "writer")
    private List<TtokTtok.Backend.domain.community.PreNotice> preNotices;

    @OneToMany(mappedBy = "requester")
    private List<TtokTtok.Backend.domain.knock.KnockRequest> knockRequests;

    @OneToMany(mappedBy = "responder")
    private List<TtokTtok.Backend.domain.knock.KnockResponse> knockResponses;

    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL)
    private List<KnockNotificationTarget> knockNotificationTargets = new ArrayList<>();

    // --- 수정된 필드 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TrustIndex> trustIndices = new ArrayList<>(); // trustIndexLogs -> trustIndices, List<TrustIndexLog> -> List<TrustIndex>
    // --- 수정 종료 ---

    // 신뢰 지수 업데이트 편의 메서드
    public void addTrustIndex(Double amount, String description) {
        this.trustIndex += amount;
        // 수정된 TrustIndex 엔티티 사용
        TrustIndex log = new TrustIndex(this, amount, description);
        this.trustIndices.add(log); // trustIndexLogs -> trustIndices
    }

}



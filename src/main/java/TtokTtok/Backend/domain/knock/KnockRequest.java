package TtokTtok.Backend.domain.knock;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KNOCK_REQUEST")
@Getter
@Setter
public class KnockRequest extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;

    @Column(name = "request_time", nullable = false)
    private java.time.LocalDateTime requestTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "noise_category", length = 30, nullable = false)
    private NoiseCategory noiseCategory;

    @Column(name = "location_scope")
    private Integer locationScope;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private KnockResult result;

    // --- 신규 필드 추가 ---
    @OneToMany(mappedBy = "knockRequest", cascade = CascadeType.ALL)
    private List<KnockNotificationTarget> notificationTargets = new ArrayList<>();

    // --- knockReport 필드 추가 (1:1 관계) ---
    @OneToOne(mappedBy = "knockRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private KnockReport knockReport;

}



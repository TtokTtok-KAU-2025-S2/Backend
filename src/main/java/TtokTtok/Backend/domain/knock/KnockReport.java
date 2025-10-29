package TtokTtok.Backend.domain.knock;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자에게 보고된 '똑똑' 요청 기록 엔티티
 */
@Entity
@Table(name = "KNOCK_REPORT") // 새 테이블
@Getter
@Setter
@NoArgsConstructor
public class KnockReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    // KnockRequest와 1:1 관계 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private KnockRequest knockRequest;

    @Column(name = "report_time", nullable = false, updatable = false)
    private java.time.LocalDateTime reportTime; // 보고된 시간

    // 생성자
    public KnockReport(KnockRequest knockRequest) {
        this.knockRequest = knockRequest;
        this.reportTime = java.time.LocalDateTime.now();
        // 양방향 연관관계 설정
        knockRequest.setKnockReport(this);
    }
}

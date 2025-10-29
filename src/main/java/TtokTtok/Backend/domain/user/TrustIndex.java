package TtokTtok.Backend.domain.user;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TRUST_INDEX")
@Getter @Setter
@NoArgsConstructor
public class TrustIndex extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "change_amount", nullable = false)
    private Double changeAmount;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "log_date", nullable = false)
    private java.time.LocalDateTime logDate;

    public TrustIndex(User user, Double changeAmount, String description) {
        this.user = user;
        this.changeAmount = changeAmount;
        this.description = description;
        this.logDate = java.time.LocalDateTime.now();
    }
}


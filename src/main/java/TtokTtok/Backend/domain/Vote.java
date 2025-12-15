package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.common.enums.VoteType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noise_diary_id", nullable = false)
    private NoiseDiary noiseDiary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private VoteType type;

    public void updateType(VoteType type) {
        this.type = type;
    }
}
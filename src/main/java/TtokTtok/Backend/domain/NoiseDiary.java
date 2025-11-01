package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.common.enums.NoiseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NoiseDiary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noise_diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ERD에 duration이 DATETIME으로 되어있으나, '측정 소요시간'이므로 '초(seconds)' 단위의 Integer로 구현
    @Column(nullable = false)
    private Integer duration;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal dbHigh;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal dbAvg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoiseCategory category;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String summary;

    @Column(nullable = false)
    private Boolean reportYn;

    @Column(nullable = true)
    private LocalDateTime reportedAt;

    // --- 양방향 연관관계 (CascadeType.ALL: 일기 삭제 시 관련 투표/댓글 모두 삭제) ---

    @OneToMany(mappedBy = "noiseDiary", cascade = CascadeType.ALL)
    private List<Vote> voteList = new ArrayList<>();

    @OneToMany(mappedBy = "noiseDiary", cascade = CascadeType.ALL)
    private List<ReportComment> reportCommentList = new ArrayList<>();
}
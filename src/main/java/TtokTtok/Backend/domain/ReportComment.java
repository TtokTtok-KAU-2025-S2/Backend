package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReportComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noise_diary_id", nullable = false)
    private NoiseDiary noiseDiary;

    @Column(length = 500, nullable = false)
    private String content;

     public void updateContent(String content) {
        this.content = content;
     }
}

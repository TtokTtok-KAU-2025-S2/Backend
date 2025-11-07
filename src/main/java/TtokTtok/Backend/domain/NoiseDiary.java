package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class NoiseDiary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noise_diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 512, nullable = true) // S3 URL 또는 로컬 경로를 저장할 충분한 길이
    private String audioFilePath;

    // ERD에 duration이 DATETIME으로 되어있으나, '측정 소요시간'이므로 '초(seconds)' 단위의 Integer로 구현
    @Column(nullable = false)
    private Integer duration;  //소음 지속 시간(녹음 duration)

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal dbHigh;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal dbAvg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoiseCategory category;   // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoiseGrade grade;  //소음 등급(QUIET, NORMAL, LOUD)

    @Column(length = 500)
    private String description;   //사용자가 소음 관련 내용을 입력

    @Column(length = 500)
    private String summary;   // AI가 소음일기 내용을 작성해줌

    @Column(nullable = false)
    private Boolean reportYn;    //소음기록을 소음현황판 기능으로 전달했을 경우, boolean 값으로 true

    @Column(nullable = true)
    private LocalDateTime reportedAt; //소음기록을 소음현황판 기능으로 전달한 시각

    //BaseEntity 수정하면 안 됨 -> noise 도메인 내에서는 소음 발생 시간 occuredAt를 이용/ 수정  시, updateAt에서 occuredAt으로
    //소음 발생 시각(소음일기 생성 시각)
    @Column(nullable = true)
    private LocalDateTime occuredAt;

    //소음일기 수정 시각
    @Column(nullable = true)
    private LocalDateTime updateAt;

    // soft delete 플래그 추가
    @Column(nullable = false)
    private Boolean deleted = false;

    // --- 양방향 연관관계 (CascadeType.ALL: 일기 삭제 시 관련 투표/댓글 모두 삭제) ---

    @OneToMany(mappedBy = "noiseDiary", cascade = CascadeType.ALL)
    private List<Vote> voteList = new ArrayList<>();

    @OneToMany(mappedBy = "noiseDiary", cascade = CascadeType.ALL)
    private List<ReportComment> reportCommentList = new ArrayList<>();
}
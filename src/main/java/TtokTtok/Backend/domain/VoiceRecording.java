package TtokTtok.Backend.domain;

import TtokTtok.Backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VoiceRecording extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 녹음한 사용자

    @Column(length = 511, nullable = false)
    private String fileUrl; // S3에 저장된 URL

    private String originalFileName;

    // 필요시 duration (녹음 시간) 등 필드 추가

    // ✨ [추가] 메타데이터 필드
    private Integer duration; // 지속 시간 (초)
    private Double dbMax;     // 최대 데시벨
    private Double dbAvg;     // 평균 데시벨
}
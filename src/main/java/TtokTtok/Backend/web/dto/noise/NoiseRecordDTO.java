package TtokTtok.Backend.domain.noise.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoiseRecordDTO {
   // private Long userId;
    private Long noiseId;          // 소음일기 고유 ID
    private String category;           // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )
    private String createdAt;          // 소음 생성 시각
    private String noiseGrade;           //소음 등급(QUIET, NORMAL, LOUD)
    private double dbHigh;              // 최대 데시벨
    private double dbAvg;              // 평균 데시벨
    private String summary;             // AI가 생성한 소음 메모
    private String modifiedAt;

}


package TtokTtok.Backend.web.dto.noise;


import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoiseRecordDTO {
    private Long userId;
    private Long noiseId;          // 소음일기 고유 ID
    private NoiseCategory category;           // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )
    private LocalDateTime occuredAt;          // 소음 생성 시각
    private NoiseGrade noiseGrade;           //소음 등급(QUIET, NORMAL, LOUD)
    private BigDecimal dbHigh;              // 최대 데시벨
    private BigDecimal dbAvg;              // 평균 데시벨
    private String description;        //사용자가 작성한 소음 메모
    private LocalDateTime updatedAt;
    private Integer duration;

}


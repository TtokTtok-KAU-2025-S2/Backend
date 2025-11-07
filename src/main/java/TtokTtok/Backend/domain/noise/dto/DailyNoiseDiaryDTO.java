package TtokTtok.Backend.domain.noise.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyNoiseDiaryDTO {
    private Long userId;
    private Integer year;
    private Integer month;
    private List<NoiseRecord> records;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoiseRecord {
        private Long recordId;
        private NoiseCategory category;           // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )
        private LocalDateTime occuredAt;          // 소음 생성 시각(소음 기록 생성 시각)
        private LocalDateTime updatedAt;          // 소음 기록 수정 시각
        private NoiseGrade grade;               //소음 등급(QUIET, NORMAL, LOUD)
        private BigDecimal dbHigh;              // 최대 데시벨
        private BigDecimal dbAvg;              // 평균 데시벨
        private String summary;             // AI가 생성한 소음 메모
    }
}

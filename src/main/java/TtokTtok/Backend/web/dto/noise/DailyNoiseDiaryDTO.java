package TtokTtok.Backend.web.dto.noise;

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
        private NoiseCategory category;
        private LocalDateTime occuredAt;
        private LocalDateTime updatedAt;
        private NoiseGrade grade;
        private BigDecimal dbHigh;
        private BigDecimal dbAvg;
        // private String summary; // 삭제: AI 요약 안 보여줌
        private String description; // ✨ 추가: 사용자 메모 표시
        private Integer duration;

        private Boolean reportYn;
    }
}
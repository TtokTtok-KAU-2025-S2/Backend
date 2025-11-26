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
public class NoiseRecordUpdateDTO {
    private NoiseCategory category;
    private LocalDateTime occuredAt;
    private NoiseGrade noiseGrade;
    private BigDecimal dbHigh;
    private BigDecimal dbAvg;
    // private String summary;  // 삭제: AI 요약 대신 사용자 메모만 수정
    private String description; // ✨ 사용자 작성 메모
    private LocalDateTime updatedAt;
}
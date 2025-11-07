package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisRequestDTO {
    // 오디오 파일은 RequestPart로 별도 수신
    private BigDecimal dbAvg;
    private NoiseCategory category;
    private String description;
    private LocalDateTime occuredAt;
}

package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class NoiseDiaryRequestDTO { private Long userId;
    private LocalDateTime occuredAt;
    private Integer duration;
    private BigDecimal dbHigh;
    private BigDecimal dbAvg;
    private NoiseCategory category;
    private NoiseGrade grade;
    private String description;

}

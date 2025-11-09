package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class NoiseDiaryResponseDTO {
    private Long id;
    private Long userId;
    private Integer duration;
    private BigDecimal dbHigh;
    private BigDecimal dbAvg;
    private NoiseCategory category;
    private NoiseGrade grade;
    private String description;
    private String summary;
    private LocalDateTime occuredAt;
    private LocalDateTime updateAt;

}

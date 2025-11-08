package TtokTtok.Backend.domain.noise.dto;

//HardDELETE 시 필요함

import TtokTtok.Backend.common.enums.NoiseCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NoiseRecordDeleteDTO {
    private Long noiseId;          // 소음일기 고유 ID
    private NoiseCategory category;           // 소음 카테고리 ( FOOTSTEPS, HAMMERING, FURNITURE, MUSIC, UNKNOWN )
    private LocalDateTime createdAt;          // 소음 생성 시각
    private LocalDateTime noiseGrade;           //소음 등급(QUIET, NORMAL, LOUD)
    private BigDecimal dbHigh;              // 최대 데시벨
    private BigDecimal dbAvg;              // 평균 데시벨
    private String summary;             // AI가 생성한 소음 메모
}

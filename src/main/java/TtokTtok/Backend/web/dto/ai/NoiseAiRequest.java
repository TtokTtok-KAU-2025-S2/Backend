package TtokTtok.Backend.web.dto.ai;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.NoiseGrade;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NoiseAiRequest {

    @Getter
    @Setter
    public static class CategoryRequest {

        @NotNull(message = "recordId는 필수입니다.")
        private Long recordId;
    }

    @Getter
    @Setter
    public static class SummaryRequest {

        @NotNull(message = "recordId는 필수입니다.")
        private Long recordId;

        @NotNull(message = "category는 필수입니다.")
        private NoiseCategory category;

        @NotBlank(message = "description은 비어 있을 수 없습니다.")
        private String description;

        @NotNull
        @DecimalMin(value = "0.0", message = "평균 데시벨은 0 이상이어야 합니다.")
        private BigDecimal dbAvg;

        @NotNull
        @DecimalMin(value = "0.0", message = "최대 데시벨은 0 이상이어야 합니다.")
        private BigDecimal dbHigh;

        @NotNull
        @Positive(message = "duration는 1초 이상이어야 합니다.")
        private Integer duration;

        private NoiseGrade noiseGrade;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime occuredAt;

        /**
         * 카테고리 분석 시 받아온 transcript를 재사용
         */
        private String transcript;
    }
}


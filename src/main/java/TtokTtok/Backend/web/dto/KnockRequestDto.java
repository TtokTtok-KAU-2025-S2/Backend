package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.ResponseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class KnockRequestDto {

    @Getter
    public static class CreateDto {
        @NotNull(message = "소음 카테고리는 필수입니다.")
        private NoiseCategory noiseCategory;
    }

    @Getter
    public static class RespondDto {
        @NotNull(message = "응답 유형은 필수입니다.")
        private ResponseType responseType;
    }
}
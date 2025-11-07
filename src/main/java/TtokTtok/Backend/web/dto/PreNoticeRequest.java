package TtokTtok.Backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

public class PreNoticeRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatePreNoticeDto {
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        @NotBlank(message = "예정일은 필수 입력값입니다.")
        @Pattern(regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}$", message = "예정일은 YYYY.MM.DD 형식이어야 합니다.")
        private String eventDate;

        @NotBlank(message = "시간은 필수 입력값입니다.")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d) ~ ([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간은 HH:MM ~ HH:MM 형식이어야 합니다.")
        private String eventTime;

        @NotBlank(message = "사유는 필수 입력 값입니다.")
        private String eventReason;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdatePreNoticeDto {
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 값입니다.")
        private String content;

        @NotBlank(message = "예정일은 필수 입력값입니다.")
        @Pattern(regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}$", message = "예정일은 YYYY.MM.DD 형식이어야 합니다.")
        private String eventDate;

        @NotBlank(message = "시간은 필수 입력값입니다.")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d) ~ ([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간은 HH:MM ~ HH:MM 형식이어야 합니다.")
        private String eventTime;

        @NotBlank(message = "사유는 필수 입력 값입니다.")
        private String eventReason;
    }
}

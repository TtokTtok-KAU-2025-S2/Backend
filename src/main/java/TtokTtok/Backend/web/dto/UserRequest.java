package TtokTtok.Backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

public class UserRequest {

    @Getter
    public static class SignUpDTO {
        @NotBlank(message = "닉네임은 필수입니다")
        private String nickname;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Pattern(regexp = "^[A-Z][a-zA-Z0-9!@#$%^&*]{7,}$", message = "비밀번호는 영문 대문자로 시작하고, 8자 이상이며, 숫자와 기호를 포함해야 합니다")
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수입니다")
        private String passwordConfirm;

        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @NotBlank(message = "아파트 고유 코드는 필수입니다")
        private String aptCode;

        @NotNull(message = "동은 필수입니다")
        private Integer dong;

        @NotNull(message = "호수는 필수입니다")
        private Integer ho;
    }

    @Getter
    public static class LoginDTO {
        @NotBlank(message = "닉네임은 필수입니다")
        private String nickname;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    @Getter
    public static class ManagerContactDTO {
        @NotBlank(message = "아파트 이름은 필수입니다")
        private String aptName;
    }
}


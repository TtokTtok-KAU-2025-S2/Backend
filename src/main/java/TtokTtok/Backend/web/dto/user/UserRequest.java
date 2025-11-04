package TtokTtok.Backend.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class UserRequest {

    @Getter
    @Setter
    public static class JoinDto {
        @NotNull(message = "아파트 ID는 필수 입력 값입니다.")
        private Long aptId; // 사용자가 속한 아파트의 ID

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        private String email; // 로그인 아이디로 사용

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp =
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 8~20자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        @NotNull(message = "동은 필수 입력값입니다.")
        private Integer dong;

        @NotNull(message = "호수는 필수 입력값입니다.")
        private Integer hosu;
   }

   @Getter
   @Setter
   public static class LoginDto {
       @NotBlank(message = "이메일은 필수 입력 값입니다.")
       @Email(message = "이메일 형식에 맞지 않습니다.")
       private String email;

       @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
       private String password;
   }

   @Getter
   @Setter
   public static class EmailRequestDto {
       @NotBlank(message = "이메일은 필수 입력 값입니다.")
       @Email(message = "이메일 형식에 맞지 않습니다.")
       private String email;
   }

   @Getter
   @Setter
   public static class PasswordResetRequestDto {
       @NotBlank(message = "이메일은 필수 입력 값입니다.")
       @Email(message = "이메일 형식에 맞지 않습니다.")
       private String email;

       @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
       @Pattern(regexp =
               "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
               message = "비밀번호는 8~20자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야 합니다.")
       private String newPassword;
   }
}
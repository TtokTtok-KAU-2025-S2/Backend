package TtokTtok.Backend.web.dto;

import lombok.Builder;
import lombok.Getter;

public class UserResponse {

    @Getter
    @Builder
    public static class SignUpResultDTO {
        private Long userId;
        private String nickname;
        private String email;
        private String aptName;
        private Integer dong;
        private Integer ho;
    }

    @Getter
    @Builder
    public static class LoginResultDTO {
        private Long userId;
        private String nickname;
        private String email;
        private String aptName;
        private Integer dong;
        private Integer ho;
        private Boolean isManager;
    }

    @Getter
    @Builder
    public static class ManagerContactResultDTO {
        private String aptName;
        private String phoneNum;
    }
}


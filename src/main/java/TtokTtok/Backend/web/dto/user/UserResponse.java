package TtokTtok.Backend.web.dto.user;

import TtokTtok.Backend.common.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDetailDto {
        private Long id;
        private String email;
        private Integer dong;
        private Integer hosu;
        private RoleType role;
   }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long accessTokenExpiresIn;
        private UserDetailDto userDetailDto;
    }
}
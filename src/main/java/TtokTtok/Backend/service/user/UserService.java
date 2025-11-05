package TtokTtok.Backend.service.user;

import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.web.dto.user.UserRequest;
import TtokTtok.Backend.web.dto.user.UserResponse;

public interface UserService {
    User signUp(UserRequest.JoinDto joinDto);

    UserResponse.TokenInfo login(UserRequest.LoginDto loginDto);

    void issueTemporaryPassword(UserRequest.EmailRequestDto emailRequestDtoDto);

    void resetPassword(UserRequest.PasswordResetRequestDto passwordResetRequestDto);
}

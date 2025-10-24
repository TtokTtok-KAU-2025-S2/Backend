package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.UserRequest;
import TtokTtok.Backend.web.dto.UserResponse;

public interface UserCommandService {
    UserResponse.SignUpResultDTO signUp(UserRequest.SignUpDTO request);
}


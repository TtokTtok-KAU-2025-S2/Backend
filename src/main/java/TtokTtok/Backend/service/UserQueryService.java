package TtokTtok.Backend.service;

import TtokTtok.Backend.web.dto.UserRequest;
import TtokTtok.Backend.web.dto.UserResponse;

public interface UserQueryService {
    UserResponse.LoginResultDTO login(UserRequest.LoginDTO request);
    UserResponse.ManagerContactResultDTO getManagerContact(UserRequest.ManagerContactDTO request);
}


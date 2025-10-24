package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.apiPayload.code.status.SuccessStatus;
import TtokTtok.Backend.service.UserCommandService;
import TtokTtok.Backend.service.UserQueryService;
import TtokTtok.Backend.web.dto.UserRequest;
import TtokTtok.Backend.web.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserRestController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse.SignUpResultDTO> signUp(@Valid @RequestBody UserRequest.SignUpDTO request) {
        UserResponse.SignUpResultDTO result = userCommandService.signUp(request);
        return ApiResponse.of(SuccessStatus.SIGNUP_SUCCESS, result);
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse.LoginResultDTO> login(@Valid @RequestBody UserRequest.LoginDTO request) {
        UserResponse.LoginResultDTO result = userQueryService.login(request);
        return ApiResponse.of(SuccessStatus.LOGIN_SUCCESS, result);
    }

    @PostMapping("/manager-contact")
    public ApiResponse<UserResponse.ManagerContactResultDTO> getManagerContact(@Valid @RequestBody UserRequest.ManagerContactDTO request) {
        UserResponse.ManagerContactResultDTO result = userQueryService.getManagerContact(request);
        return ApiResponse.of(SuccessStatus.MANAGER_CONTACT_FOUND, result);
    }
}


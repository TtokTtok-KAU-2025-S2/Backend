package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.service.user.UserService;
import TtokTtok.Backend.web.dto.user.UserRequest;
import TtokTtok.Backend.web.dto.user.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

     private final UserService userService;

     @PostMapping("/join")
     public ApiResponse<UserResponse.UserDetailDto> join(@RequestBody @Valid UserRequest.JoinDto request) {
         User user = userService.signUp(request);
         return ApiResponse.onSuccess(UserResponse.UserDetailDto.builder()
                 .id(user.getId())
                 .email(user.getEmail())
                 .dong(user.getDong())
                 .hosu(user.getHosu())
                 .role(user.getRole())
                 .build());
     }

    @GetMapping("/verify-email")
    public void verifyEmail(@RequestParam("email") String email, @RequestParam("code") String code, HttpServletResponse response)
            throws IOException {
        userService.verifyEmail(email, code);
        // 기말 시험 끝나고 희승님이 주실 url 붙여넣기
        response.sendRedirect("https://your-frontend-app.com/auth/verification-success");
    }

     @PostMapping("/login")
     public ApiResponse<UserResponse.TokenInfo> login(@RequestBody @Valid UserRequest.LoginDto request) {
         UserResponse.TokenInfo tokenInfo = userService.login(request);
         return ApiResponse.onSuccess(tokenInfo);
     }

     @PostMapping("/issue-temporary-password")
     public ApiResponse<String> issueTemporaryPassword(@RequestBody @Valid UserRequest.EmailRequestDto request) {
         userService.issueTemporaryPassword(request);
         return ApiResponse.onSuccess("임시 비밀번호가 이메일로 발송되었습니다.");
     }

     @PostMapping("/reset-password")
     public ApiResponse<String> resetPassword(@RequestBody @Valid UserRequest.PasswordResetRequestDto request) {
         userService.resetPassword(request);
         return ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다.");
     }
}
package TtokTtok.Backend.service.user;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.common.enums.RoleType;
import TtokTtok.Backend.config.jwt.JwtTokenProvider;
import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.ApartmentRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.service.mail.EmailService;
import TtokTtok.Backend.web.dto.user.UserRequest;
import TtokTtok.Backend.web.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

     private final UserRepository userRepository;
     private final ApartmentRepository apartmentRepository;
     private final PasswordEncoder passwordEncoder;
     private final AuthenticationManagerBuilder authenticationManagerBuilder;
     private final JwtTokenProvider jwtTokenProvider;
     private final EmailService emailService;

     @Override
     public User signUp(UserRequest.JoinDto joinDto) {
         if (userRepository.existsByEmail(joinDto.getEmail())) {
             throw new GeneralException(ErrorStatus.EMAIL_ALREADY_EXISTS);
         }

         Apartment apartment = apartmentRepository.findById(joinDto.getAptId())
                 .orElseThrow(() -> new GeneralException(ErrorStatus.APARTMENT_NOT_FOUND));

         String verificationCode = UUID.randomUUID().toString();

         User newUser = User.builder()
                 .email(joinDto.getEmail())
                 .password(passwordEncoder.encode(joinDto.getPassword()))
                 .dong(joinDto.getDong())
                 .hosu(joinDto.getHosu())
                 .apartment(apartment)
                 .role(RoleType.USER) // 기본 역할을 USER로 설정
                 .emailAuthCode(verificationCode)
                 .emailVerified(false)
                 .build();

         userRepository.save(newUser);
         emailService.sendVerificationEmail(newUser.getEmail(), verificationCode);

         return newUser;
     }

     @Override
     public UserResponse.TokenInfo login(UserRequest.LoginDto loginDto) {
         User user = userRepository.findByEmail(loginDto.getEmail())
                 .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

         // 이메일 인증 여부 확인
         if (!user.isEmailVerified()) {
             throw new GeneralException(ErrorStatus.EMAIL_NOT_VERIFIED);
         }

         // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
         UsernamePasswordAuthenticationToken authenticationToken =
                 new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

         // 2. 실제 검증 (사용자 비밀번호 체크)
         // CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행됨
         Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

         // 3. 인증 정보를 기반으로 JWT 토큰 생성
         return jwtTokenProvider.generateToken(authentication);
     }

     @Override
     public void issueTemporaryPassword(UserRequest.EmailRequestDto emailRequestDto) {
         User user = userRepository.findByEmail(emailRequestDto.getEmail())
                 .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

         String temporaryPassword = UUID.randomUUID().toString().substring(0, 8); // 8자리 임시 비밀번호 생성
         user.updatePassword(passwordEncoder.encode(temporaryPassword));
         userRepository.save(user); // 변경된 비밀번호 저장

         emailService.sendTemporaryPasswordEmail(user.getEmail(), temporaryPassword);
     }

     @Override
     public void resetPassword(UserRequest.PasswordResetRequestDto passwordResetRequestDto) {
         User user = userRepository.findByEmail(passwordResetRequestDto.getEmail())
                 .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

         user.updatePassword(passwordEncoder.encode(passwordResetRequestDto.getNewPassword()));
         userRepository.save(user);
     }

    @Override
    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        if (user.getEmailAuthCode() != null && user.getEmailAuthCode().equals(code)) {
            user.verifyEmail();
            userRepository.save(user);
        } else {
            throw new GeneralException(ErrorStatus.INVALID_VERIFICATION_CODE);
        }
    }
}
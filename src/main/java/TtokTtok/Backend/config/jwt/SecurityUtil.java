package TtokTtok.Backend.config.jwt;

import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    // 1. 정적 필드: static 메서드에서 접근하기 위해 필요
    private static UserRepository staticUserRepository;

    // 2. 인스턴스 필드: Spring이 @Autowired로 주입
    @Autowired
    private UserRepository userRepository;

    // 3. PostConstruct: 인스턴스 주입 후 정적 필드에 할당
    @PostConstruct
    public void setStaticUserRepository() {
        SecurityUtil.staticUserRepository = this.userRepository;
    }


    public static Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 anonymousUser인 경우
        if (authentication == null ||
                authentication.getName() == null ||
                "anonymousUser".equals(authentication.getName())) {
            // 로그인 안 된 상태 or 잘못된 토큰
            throw new IllegalArgumentException("MEMBER4001");
        }

        String userEmail = authentication.getName();

        User user = staticUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER4001"));

        return user.getId();
    }


    private SecurityUtil() {}

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request가 들어올 때 JwtFilter의 doFilter에서 저장
    public static String getCurrentUserEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }
        return authentication.getName();
    }
}

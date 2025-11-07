package TtokTtok.Backend.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. Slf4j 임포트 추가
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j // 2. 로그를 찍기 위해 어노테이션 추가
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // ------------------- (디버깅 로그 추가) -------------------
        if (token == null) {
            log.warn("[JWT 필터] 토큰이 없습니다. (Authorization 헤더 누락 또는 Bearer 아님)");
        } else {
            log.info("[JWT 필터] 토큰 발견. 유효성 검사 시작: {}", token);
            if (jwtTokenProvider.validateToken(token)) {
                log.info("[JWT 필터] 토큰 유효함. SecurityContext에 인증 정보 저장.");
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("[JWT 필터] 토큰이 유효하지 않습니다. (만료, 서명 불일치 등)");
            }
        }
        // -----------------------------------------------------

        chain.doFilter(request, response);
    }

    //Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
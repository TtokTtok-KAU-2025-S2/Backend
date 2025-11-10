package TtokTtok.Backend.config.jwt;

import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.user.UserResponse;
import TtokTtok.Backend.web.dto.user.UserResponse.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtTokenProvider(@Value("VlwEyVBsYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHa") String secretKey,
                            @Value("3600") long accessTokenValidity,
                            @Value("86400") long refreshTokenValidity,
                            UserDetailsService userDetailsService,
                            UserRepository userRepository)
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInSeconds = accessTokenValidity * 1000;
        this.refreshTokenValidityInSeconds = refreshTokenValidity * 1000;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }
    public TokenInfo generateToken(Authentication authentication) {
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));

        UserResponse.UserDetailDto userDetailDto = UserResponse.UserDetailDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .dong(user.getDong())
                .hosu(user.getHosu())
                .role(user.getRole())
                .build();


        long now = (new Date()).getTime();
        // Access Token 생성~
        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInSeconds);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenValidityInSeconds))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .userDetailDto(userDetailDto)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // (기존 코드 삭제)
        // Collection<? extends GrantedAuthority> authorities = ...
        // UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 6. (수정) DB에서 "진짜" UserDetails(TtokTtok.Backend.domain.User)를 가져옴
        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());

        // 7. (수정) principal.getAuthorities()를 사용하여 권한 설정
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    // 토큰 정보 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token.", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }
        catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}

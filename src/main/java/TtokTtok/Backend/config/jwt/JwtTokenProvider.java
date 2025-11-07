package TtokTtok.Backend.config.jwt;

import TtokTtok.Backend.web.dto.user.UserResponse.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityInMillis;
    private final long refreshTokenValidityInMillis;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("VlwEyVBsYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHa") String secretKey,
            @Value("3600") long accessTokenValidity,   // 초 단위
            @Value("86400") long refreshTokenValidity, // 초 단위
            UserDetailsService userDetailsService
    ) {
        // 1) secretKey가 Base64라면 아래 코드 사용
        // byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // 2) 그냥 평문 키를 쓰고 싶다면 이걸로 (둘 중 하나 선택)
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMillis = accessTokenValidity * 1000;     // 초 → ms
        this.refreshTokenValidityInMillis = refreshTokenValidity * 1000;   // 초 → ms
        this.userDetailsService = userDetailsService;
    }

    public TokenInfo generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(java.util.stream.Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInMillis);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshTokenValidityInMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // DB에서 실제 UserDetails 로드
        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());

        // principal.getAuthorities() 사용
        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                principal.getAuthorities()
        );
    }

    // 토큰 정보 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
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
        // 예외가 발생하면 false
        return false;
    }

    // Claims 파싱용 메서드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이라도 내부 정보(claims)는 쓸 수 있게
            return e.getClaims();
        }
    }
}
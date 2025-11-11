package com.example.routie_be.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    // application.properties 또는 application.yml에서 주입받을 JWT 비밀 키
    @Value("${jwt.secret-key}")
    private String secretKeyString;
    @Value("${jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMs;

    private Key secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMs);

        Claims claims = Jwts.claims()
                .setSubject(email)
                .add("userId", userId)
                .build();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰을 복호화하여 인증 객체(Authentication)를 생성합니다.
     * Principal에 Long 타입의 userId를 설정합니다.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 1. Long 타입의 userId 클레임을 추출 (Controller의 @AuthenticationPrincipal Long userId에 주입될 값)
        Long userId = claims.get("userId", Long.class);

        if (userId == null) {
            // userId 클레임이 누락된 경우, 인증은 실패로 처리하는 것이 안전합니다.
            // Spring Security 필터 체인에서 이 예외를 처리하도록 할 수 있습니다.
            throw new JwtException("User ID claim (userId) is missing or invalid in token.");
        }

        // 2. 권한 목록 생성 (최소 권한 부여)
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        // 3. Long 타입의 userId를 Principal로 설정하여 Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(
                userId, // 💡 Long userId가 Principal로 설정됨
                "",
                authorities
        );
    }

    public long getExpiration(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.getTime() - new Date().getTime();
    }
}
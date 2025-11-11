package com.example.routie_be.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret-key:${JWT_SECRET_KEY}}")
    private String secretKeyString;

    @Value(
            "${jwt.access-token-expiration-milliseconds:${JWT_ACCESS_TOKEN_EXPIRATION_MILLISECONDS:3600000}}")
    private long accessTokenExpirationMs;

    private Key secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMs);

        Claims claims = Jwts.claims().setSubject(email).add("userId", userId).build();

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
            Jws<Claims> claims =
                    Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims =
                Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

        // 1. Long 타입의 userId 클레임 추출
        Long userId = claims.get("userId", Long.class);

        if (userId == null) {
            throw new JwtException("User ID claim (userId) is missing or invalid in token.");
        }

        // 2. 권한 목록 생성 (최소 권한 부여)
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // 3. Long 타입의 userId를 Principal로 설정하여 Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(userId, "", authorities);
    }

    public long getExpiration(String token) {
        Date expiration =
                Jwts.parser()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getExpiration();
        return expiration.getTime() - new Date().getTime();
    }
}

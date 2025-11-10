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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // application.properties 또는 application.yml에서 주입받을 JWT 비밀 키
    @Value("${jwt.secret-key}")
    private String secretKeyString;

    // 토큰 만료 시간 (예: 1시간 = 3,600,000ms)
    @Value("${jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMs;

    private Key secretKey;

    /**
     * 클래스 초기화 시, secretKey를 Base64 디코딩하여 Key 객체로 만듭니다.
     */
    @PostConstruct
    protected void init() {
        // 비밀 키 문자열을 바이트 배열로 변환하고, HMAC SHA 키로 사용
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    /**
     * 엑세스 토큰(Access Token)을 생성합니다.
     * @param userId 사용자 ID
     * @param email 사용자 이메일 (Subject로 사용)
     * @return 생성된 JWT 문자열
     */
    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMs);

        Claims claims = Jwts.claims()
                .setSubject(email)
                .add("userId", userId) // 💡 add()를 사용하여 커스텀 클레임 추가
                .build();              // 💡 build()는 마지막에 호출하여 불변 객체 생성

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 이메일(Subject)을 추출합니다.
     */
    public String getEmail(String token) {
        return Jwts.parser() // 💡 Jwts.parserBuilder() 대신 Jwts.parser() 사용
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰의 유효성 + 만료일자 확인
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            // 만료일자가 현재보다 이후인지 확인
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 경우
            return false;
        } catch (Exception e) {
            // 그 외 유효하지 않은 토큰일 경우
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 클레임(Claims) 추출
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 2. 토큰에 저장된 권한 정보 (여기서는 ROLE을 단순 USER로 가정)
        // 실제 프로젝트에서는 claims.get("roles") 등을 사용하여 동적으로 가져와야 함
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        // 3. UserDetails 객체 생성 (여기서는 이메일과 권한만 사용)
        // Spring Security의 User 클래스를 사용하거나, 커스텀 UserDetails 구현체를 사용합니다.
        // 여기서는 토큰의 이메일을 Principal로 사용합니다.

        // Principal: 토큰의 주체(이메일)
        String principal = claims.getSubject();

        // 4. 최종적으로 Authentication 객체 생성 후 반환
        return new UsernamePasswordAuthenticationToken(
                principal, // Principal (사용자 식별 정보: 이메일)
                "",        // Credentials (비밀번호: 토큰 기반이므로 빈 문자열)
                Collections.singleton(authority) // Authorities (권한 목록)
        );
    }

    public long getExpiration(String token) {
        // 토큰에서 만료 시간을 가져옴
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        // 현재 시간과의 차이를 계산하여 남은 시간 (밀리초) 반환
        return expiration.getTime() - new Date().getTime();
    }
}
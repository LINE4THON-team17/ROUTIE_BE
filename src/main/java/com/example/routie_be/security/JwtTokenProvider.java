package com.example.routie_be.security;

import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key:${JWT_SECRET_KEY}}")
    private String secretKeyString;

    @Value(
            "${jwt.access-token-expiration-milliseconds:${JWT_ACCESS_TOKEN_EXPIRATION_MILLISECONDS:3600000}}")
    private long accessTokenExpirationMs;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMs);

        Claims claims = Jwts.claims().subject(email).add("userId", userId).build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }

    public boolean validateToken(String token) {
        try {
            Date exp = parse(token).getPayload().getExpiration();
            return exp != null && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Number n = parse(token).getPayload().get("userId", Number.class);
        return n != null ? n.longValue() : null;
    }

    public String getEmail(String token) {
        return parse(token).getPayload().getSubject();
    }

    public long getExpiration(String token) {
        Date exp = parse(token).getPayload().getExpiration();
        return exp.getTime() - System.currentTimeMillis();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = parse(token).getPayload();
        Long userId =
                Optional.ofNullable(claims.get("userId", Number.class))
                        .map(Number::longValue)
                        .orElse(null);
        String email = claims.getSubject();

        if (userId == null || email == null) {
            throw new JwtException("Missing claims (userId/subject)");
        }

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var principal = new UserPrincipal(userId, email, authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}

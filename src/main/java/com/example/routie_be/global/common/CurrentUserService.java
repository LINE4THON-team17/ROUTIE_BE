package com.example.routie_be.global.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.routie_be.security.UserPrincipal;

@Component
public class CurrentUserService {

    /** 인증된 사용자 id (없으면 null) */
    public Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal up) {
            return up.id();
        }

        if (principal instanceof Number n) {
            return n.longValue();
        }
        return null;
    }

    /** 인증된 사용자 이메일 (없으면 null) */
    public String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal up) {
            return up.email();
        }
        if (principal instanceof String s) { // 구버전 호환
            return s;
        }
        return null;
    }
}

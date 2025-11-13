package com.example.routie_be.global.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.routie_be.security.UserPrincipal;

@Service
public class CurrentUserService {

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보(User ID)를 가져올 수 없습니다. 유효한 토큰인지 확인하세요.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.id();
        }

        throw new RuntimeException("인증 정보(User ID)를 가져올 수 없습니다. 유효한 토큰인지 확인하세요.");
    }
}

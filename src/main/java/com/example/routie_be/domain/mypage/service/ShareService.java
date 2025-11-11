package com.example.routie_be.domain.mypage.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.mypage.dto.ShareProfileResponse;
import com.example.routie_be.domain.mypage.entity.UserProfileShare;
import com.example.routie_be.domain.mypage.repository.ShareRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {

    private final ShareRepo shareRepo;
    private static final String PREFIX = "routie-";

    private String newSlug() {
        byte[] b = new byte[6]; // 6 bytes ≈ 8 Base64URL chars
        new SecureRandom().nextBytes(b);
        String core = Base64.getUrlEncoder().withoutPadding().encodeToString(b); // 예: "Jc2Aqf8"
        return PREFIX + core.toLowerCase();
    }

    @Transactional
    public ShareProfileResponse issueSlug(Long userId) {
        // 이미 있으면 재사용
        return shareRepo
                .findByUserId(userId)
                .map(s -> new ShareProfileResponse(userId, s.getSlug()))
                .orElseGet(
                        () -> {
                            String slug;
                            do {
                                slug = newSlug();
                            } while (shareRepo.existsBySlug(slug));
                            UserProfileShare saved =
                                    shareRepo.save(new UserProfileShare(userId, slug));
                            return new ShareProfileResponse(userId, saved.getSlug());
                        });
    }

    @Transactional
    public Long resolveUser(String slug) {
        return shareRepo
                .findBySlug(slug)
                .map(
                        s -> {
                            s.setLastAccessedAt(LocalDateTime.now()); // 더티체킹으로 자동 업데이트
                            return s.getUserId();
                        })
                .orElse(null);
    }
}
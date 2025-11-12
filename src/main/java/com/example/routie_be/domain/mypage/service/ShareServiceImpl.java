package com.example.routie_be.domain.mypage.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.mypage.config.ShareProperties;
import com.example.routie_be.domain.mypage.dto.ShareProfileResponse;
import com.example.routie_be.domain.mypage.entity.UserProfileShare;
import com.example.routie_be.domain.mypage.repository.ShareRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepo repo;
    private final ShareProperties props;
    private final Clock clock = Clock.systemDefaultZone();
    private static final char[] B62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final SecureRandom RNG = new SecureRandom();

    @Override
    @Transactional
    public ShareProfileResponse issueSlug(Long userId) {
        Optional<UserProfileShare> existing = repo.findByUserId(userId);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        String slug = generateUniqueSlug(props.slugLength());
        UserProfileShare saved = repo.save(new UserProfileShare(userId, slug));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public Long resolveUser(String slug) {
        return repo.findBySlug(slug)
                .map(
                        entity -> {
                            entity.setLastAccessedAt(LocalDateTime.now(clock));
                            return entity.getUserId();
                        })
                .orElse(null);
    }

    private String generateUniqueSlug(int len) {
        for (int i = 0; i < 20; i++) {
            String candidate = randomBase62(len);
            if (!repo.existsBySlug(candidate)) return candidate;
        }
        return generateUniqueSlug(len + 1);
    }

    private String randomBase62(int len) {
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) buf[i] = B62[RNG.nextInt(B62.length)];
        return new String(buf);
    }

    private ShareProfileResponse toResponse(UserProfileShare s) {
        String path = props.pathTemplate().formatted(s.getSlug());
        String url = props.baseUrl() + path;
        return new ShareProfileResponse(s.getUserId(), s.getSlug(), url, s.getCreatedAt());
    }
}

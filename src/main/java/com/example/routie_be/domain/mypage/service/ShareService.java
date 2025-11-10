package com.example.routie_be.domain.mypage.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.example.routie_be.domain.mypage.dto.ShareProfileResponse;
import com.example.routie_be.domain.mypage.entity.UserProfileShare;
import com.example.routie_be.domain.mypage.repository.ShareRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShareService {
    private final ShareRepo shareRepo;

    public ShareProfileResponse issueSlug(Long userId) {
        return shareRepo
                .findByUserId(userId)
                .map(s -> new ShareProfileResponse(userId, s.getSlug()))
                .orElseGet(
                        () -> {
                            String slug =
                                    "routie-"
                                            + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
                            UserProfileShare entity = new UserProfileShare(userId, slug);
                            shareRepo.save(entity);
                            return new ShareProfileResponse(userId, slug);
                        });
    }

    public Long resolveUser(String slug) {
        return shareRepo.findBySlug(slug).map(UserProfileShare::getUserId).orElse(null);
    }
}

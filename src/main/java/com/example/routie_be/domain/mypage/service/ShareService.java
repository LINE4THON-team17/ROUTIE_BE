package com.example.routie_be.domain.mypage.service;

import com.example.routie_be.domain.mypage.dto.ShareProfileResponse;

public interface ShareService {
    ShareProfileResponse issueSlug(Long userId);

    Long resolveUser(String slug);
}

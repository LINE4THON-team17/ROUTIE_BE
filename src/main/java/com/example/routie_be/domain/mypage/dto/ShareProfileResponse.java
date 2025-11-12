package com.example.routie_be.domain.mypage.dto;

import java.time.LocalDateTime;

public record ShareProfileResponse(
        Long userId, String slug, String shareUrl, LocalDateTime issuedAt) {}

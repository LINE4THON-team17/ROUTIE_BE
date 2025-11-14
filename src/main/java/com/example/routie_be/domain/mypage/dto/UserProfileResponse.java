package com.example.routie_be.domain.mypage.dto;

public record UserProfileResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        long routesCount,
        long friendsCount,
        boolean isFriend) {}

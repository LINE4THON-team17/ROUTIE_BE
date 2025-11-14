package com.example.routie_be.domain.mypage.dto;

public record UserMeResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        long routesCount,
        long savedCount,
        long friendsCount) {}

package com.example.routie_be.domain.mypage.dto;

public record UserMeResponse(
    Long id,
    String name,
    String profileImageUrl,
    long routesCount,
    long savedCount,
    long friendsCount
) {}
package com.example.routie_be.domain.mypage.dto;

public record RouteSummary(
    Long id,
    String title,
    String authorName,
    String thumbnailUrl
) {}
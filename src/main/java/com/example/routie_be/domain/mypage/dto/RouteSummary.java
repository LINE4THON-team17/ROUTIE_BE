package com.example.routie_be.domain.mypage.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RouteSummary(
        Long id, String title, String authorName, String thumbnailUrl, LocalDateTime createdAt) {
    public RouteSummary(Long id, String title, LocalDateTime createdAt) {
        this(id, title, null, null, createdAt);
    }
}

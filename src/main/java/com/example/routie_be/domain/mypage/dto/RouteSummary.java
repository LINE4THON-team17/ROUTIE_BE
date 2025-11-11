package com.example.routie_be.domain.mypage.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RouteSummary(
        Long id, String title, String authorName, String thumbnailUrl, LocalDateTime createdAt) {
    // 3개 인자용 오버로드: authorName/thumbnailUrl은 일단 null로
    public RouteSummary(Long id, String title, LocalDateTime createdAt) {
        this(id, title, null, null, createdAt);
    }
}

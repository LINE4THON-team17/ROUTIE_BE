package com.example.routie_be.domain.ai.dto;

import java.util.List;

public record RecommendItem(
    String title,
    List<String> keywords,
    List<RecommendPlace> places
) {}
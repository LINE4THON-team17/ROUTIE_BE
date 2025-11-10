package com.example.routie_be.domain.ai.dto;

import java.util.List;

public record RecommendRequest(
    List<String> keywords
) {}
package com.example.routie_be.domain.ai.service.provider;

import java.util.List;

import com.example.routie_be.domain.ai.dto.RecommendItem;

public interface AiProvider {
    List<RecommendItem> recommend(List<String> keywords, Long userId);
}

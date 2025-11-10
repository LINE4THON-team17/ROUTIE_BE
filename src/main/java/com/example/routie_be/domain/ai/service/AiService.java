package com.example.routie_be.domain.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.routie_be.domain.ai.dto.RecommendItem;
import com.example.routie_be.domain.ai.service.provider.AiProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiProvider aiProvider;

    public List<RecommendItem> recommend(List<String> keywords, Long userId) {
        return aiProvider.recommend(keywords, userId);
    }
}

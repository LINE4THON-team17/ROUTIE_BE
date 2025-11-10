package com.example.routie_be.domain.ai.service;

import com.example.routie_be.domain.ai.dto.RecommendItem;
import com.example.routie_be.domain.ai.service.provider.AiProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

  private final AiProvider aiProvider;

  public List<RecommendItem> recommend(List<String> keywords, Long userId) {
    return aiProvider.recommend(keywords, userId);
  }
}
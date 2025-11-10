package com.example.routie_be.domain.ai.service.provider;

import com.example.routie_be.domain.ai.dto.RecommendItem;
import java.util.List;

public interface AiProvider {
  List<RecommendItem> recommend(List<String> keywords, Long userId);
}
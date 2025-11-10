package com.example.routie_be.domain.ai.service.provider;

import com.example.routie_be.domain.ai.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockAiProvider implements AiProvider {

  @Override
  public List<RecommendItem> recommend(List<String> keywords, Long userId) {
    return List.of(
        new RecommendItem(
            "서울 야경 데이트 코스",
            List.of("야경", "데이트", "카페"),
            List.of(
                new RecommendPlace("남산타워", "명소", "서울 중구 소월로 105"),
                new RecommendPlace("더현대 서울 루프탑", "카페", "서울 영등포구 여의대로 108")
            )
        ),
        new RecommendItem(
            "한강 피크닉 코스",
            List.of("피크닉", "뷰", "산책"),
            List.of(
                new RecommendPlace("뚝섬유원지", "공원", "서울 광진구 자양동 112"),
                new RecommendPlace("더리버", "카페", "서울 강남구 압구정로 8길 9")
            )
        ),
        new RecommendItem(
            "강릉 힐링 여행",
            List.of("여행", "바다", "카페"),
            List.of(
                new RecommendPlace("안목해변", "명소", "강원 강릉시 창해로 14"),
                new RecommendPlace("보사노바 커피로스터스", "카페", "강릉시 창해로14번길 20")
            )
        )
    );
  }
}
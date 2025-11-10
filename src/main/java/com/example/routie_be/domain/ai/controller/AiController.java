package com.example.routie_be.domain.ai.controller;

import com.example.routie_be.domain.ai.dto.RecommendItem;
import com.example.routie_be.domain.ai.dto.RecommendRequest;
import com.example.routie_be.domain.ai.service.AiService;
import com.example.routie_be.global.common.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
    name = "AI 추천 API",
    description = """
        사용자의 관심 키워드 기반으로 맞춤 추천 API
        """
)
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

  private final AiService aiService;
  private final CurrentUserService current;

  @Operation(
      summary = "키워드 기반 AI 추천",
      description = """
          사용자가 입력한 **키워드 목록**을 기반으로 AI가 맞춤 추천을 제공합니다.<br><br>
          - 최대 5개의 키워드를 입력할 수 있습니다.<br>
          - 각 키워드는 AI 추천 모델에 의해 분석되어 관련성 높은 결과를 반환합니다.<br>
          - 로그인된 사용자의 선호도와 기록을 함께 반영합니다.
          """,
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          description = "AI 추천 요청 바디 (키워드 배열)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RecommendRequest.class),
              examples = @ExampleObject(value = """
                  {
                    "keywords": ["카페", "한강", "피크닉"]
                  }
                  """)
          )
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "AI 추천 결과 반환 성공",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = RecommendItem.class)),
                  examples = @ExampleObject(value = """
                      [
                        {
                          "title": "한강 피크닉 루트",
                          "description": "봄날에 가볍게 산책하며 카페에서 여유를 즐길 수 있는 코스입니다.",
                          "imageUrl": "https://routie.s3.ap-northeast-2.amazonaws.com/recommend/picnic1.png",
                          "category": "루트 추천",
                          "score": 0.92
                        },
                        {
                          "title": "뚝섬 수변 카페 거리",
                          "description": "한강 근처의 감성적인 카페 거리, 휴식과 산책을 동시에!",
                          "imageUrl": "https://routie.s3.ap-northeast-2.amazonaws.com/recommend/cafe1.png",
                          "category": "장소 추천",
                          "score": 0.87
                        }
                      ]
                      """)
              )
          ),
          @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값 (예: 키워드 누락 또는 빈 배열)"),
          @ApiResponse(responseCode = "401", description = "로그인이 필요함")
      }
  )
  @PostMapping("/recommend")
  public ResponseEntity<List<RecommendItem>> recommend(@RequestBody RecommendRequest req) {
    Long userId = current.getUserId();
    return ResponseEntity.ok(aiService.recommend(req.keywords(), userId));
  }
}
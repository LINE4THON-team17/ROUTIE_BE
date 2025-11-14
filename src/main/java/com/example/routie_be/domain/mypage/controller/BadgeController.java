package com.example.routie_be.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.routie_be.domain.mypage.dto.BadgeDto;
import com.example.routie_be.domain.mypage.service.BadgeService;
import com.example.routie_be.global.common.CurrentUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 - 배지 관련 컨트롤러
 *
 * <p>루트 등록 개수에 따라 자동으로 부여되는 배지 시스템:
 * <ul>
 *   <li>루키 루터: 1~9개 등록 (첫 루트를 등록하면 획득)</li>
 *   <li>열정 루터: 10~49개 등록 (10개 이상 루트를 등록하면 획득)</li>
 *   <li>전설의 루터: 50개 이상 등록 (50개 이상 루트를 등록하면 획득)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "배지 API", description = "마이페이지에서 획득 가능한 배지 관련 API")
public class BadgeController {

  private final BadgeService badgeService;
  private final CurrentUserService current;

  /**
   * 내 배지 목록 조회
   *
   * <p>로그인한 사용자가 현재 보유 중인 배지를 반환합니다.
   */
  @Operation(
      summary = "내 배지 목록 조회",
      description =
          """
          로그인된 사용자의 루트 등록 개수에 따라 자동으로 배지 목록을 반환합니다.

          🏅 배지 조건:
          - 루키 루터: 1~9개 루트 등록 (첫 루트를 등록하면 획득)
          - 열정 루터: 10~49개 루트 등록 (10개 이상 루트를 등록하면 획득)
          - 전설의 루터: 50개 이상 루트를 등록하면 획득
          """)
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "성공적으로 내 배지 목록을 조회했습니다.",
              content =
              @Content(
                  mediaType = "application/json",
                  array =
                  @ArraySchema(
                      schema =
                      @Schema(
                          implementation =
                              BadgeDto.class)),
                  examples =
                  @ExampleObject(
                      value =
                          """
                          [
                            {
                              "id": 1,
                              "name": "루키 루터",
                              "description": "첫 루트를 등록하면 획득",
                              "minRoutes": 1,
                              "maxRoutes": 9
                            },
                            {
                              "id": 2,
                              "name": "열정 루터",
                              "description": "10개 이상 루트를 등록하면 획득",
                              "minRoutes": 10,
                              "maxRoutes": 49
                            }
                          ]
                          """)))
      })
  @GetMapping("/users/me/badges")
  public ResponseEntity<List<BadgeDto>> getMyBadges() {
    Long userId = current.getUserId();
    return ResponseEntity.ok(badgeService.myBadges(userId));
  }

  /**
   * 전체 배지 정의 목록
   *
   * <p>시스템에서 정의된 전체 배지의 종류를 반환합니다.
   */
  @Operation(
      summary = "전체 배지 정의 목록 조회",
      description =
          """
          모든 사용자가 획득할 수 있는 배지의 정의 목록을 조회합니다.

          각 배지는 루트 등록 개수에 따라 자동 부여됩니다.

          🏅 배지 목록:
          - 루키 루터: 1~9개 루트 등록 (첫 루트를 등록하면 획득)
          - 열정 루터: 10~49개 루트 등록 (10개 이상 루트를 등록하면 획득)
          - 전설의 루터: 50개 이상 루트를 등록하면 획득
          """)
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "전체 배지 정의 목록을 성공적으로 조회했습니다.",
              content =
              @Content(
                  mediaType = "application/json",
                  array =
                  @ArraySchema(
                      schema =
                      @Schema(
                          implementation =
                              BadgeDto.class)),
                  examples =
                  @ExampleObject(
                      value =
                          """
                          [
                            {
                              "id": 1,
                              "name": "루키 루터",
                              "description": "첫 루트를 등록하면 획득",
                              "minRoutes": 1,
                              "maxRoutes": 9
                            },
                            {
                              "id": 2,
                              "name": "열정 루터",
                              "description": "10개 이상 루트를 등록하면 획득",
                              "minRoutes": 10,
                              "maxRoutes": 49
                            },
                            {
                              "id": 3,
                              "name": "전설의 루터",
                              "description": "50개 이상 루트를 등록하면 획득",
                              "minRoutes": 50,
                              "maxRoutes": 1000000
                            }
                          ]
                          """)))
      })
  @GetMapping("/badges")
  public ResponseEntity<List<BadgeDto>> getBadgeDefinitions() {
    return ResponseEntity.ok(badgeService.definitions());
  }
}
package com.example.routie_be.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.routie_be.domain.mypage.dto.FollowStatusDto;
import com.example.routie_be.domain.mypage.dto.FriendDto;
import com.example.routie_be.domain.mypage.service.FriendsService;
import com.example.routie_be.global.common.CurrentUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Friends API", description = "사용자 간 팔로우 및 친구 관계 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;
    private final CurrentUserService current;

    @Operation(
            summary = "내 친구 목록 조회",
            description =
                    """
          현재 로그인한 사용자가 **팔로우한 사용자 목록**을 조회합니다.

          - 정렬/페이지 파라미터는 추후 확장 가능
          - 응답은 FriendDto 배열입니다.
          """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(
                                                                        implementation =
                                                                                FriendDto.class)),
                                        examples =
                                                @ExampleObject(
                                                        name = "friends-example",
                                                        value =
                                                                """
                      [
                        {
                          "id": 12,
                          "nickname": "루루",
                          "profileImage": "https://routie.s3.ap-northeast-2.amazonaws.com/profile/pickle.png",
                          "friendDays": 25
                        },
                        {
                          "id": 34,
                          "nickname": "티티",
                          "profileImage": "https://routie.s3.ap-northeast-2.amazonaws.com/profile/routie.png",
                          "friendDays": 3
                        }
                      ]
                      """)))
            })
    @GetMapping("/me/friends")
    public ResponseEntity<List<FriendDto>> getFriends() {
        return ResponseEntity.ok(friendsService.list(current.getUserId()));
    }

    @Operation(
            summary = "팔로우",
            description =
                    """
          지정한 사용자 **userId**를 팔로우합니다.

          - Path Variable로 대상 사용자 ID를 전달합니다.
          - 이미 팔로우 중이면 400을 반환하도록 서비스 레벨에서 처리하세요.

          예시 요청:
          - `POST /api/users/42/follow`
          """,
            responses = {
                @ApiResponse(responseCode = "200", description = "팔로우 성공"),
                @ApiResponse(responseCode = "400", description = "이미 팔로우 중인 경우"),
                @ApiResponse(responseCode = "404", description = "해당 userId가 존재하지 않음")
            })
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> follow(
            @Parameter(description = "팔로우 대상 사용자 ID", example = "42") @PathVariable Long userId) {
        friendsService.follow(current.getUserId(), userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "언팔로우",
            description =
                    """
          지정한 사용자 **userId**를 언팔로우합니다.

          - Path Variable로 대상 사용자 ID를 전달합니다.
          - 팔로우 중이 아니라면 400을 반환하도록 서비스 레벨에서 처리하세요.

          예시 요청:
          - `DELETE /api/users/42/follow`
          """,
            responses = {
                @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
                @ApiResponse(responseCode = "400", description = "팔로우 중이 아닌 경우"),
                @ApiResponse(responseCode = "404", description = "해당 userId가 존재하지 않음")
            })
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollow(
            @Parameter(description = "언팔로우 대상 사용자 ID", example = "42") @PathVariable Long userId) {
        friendsService.unfollow(current.getUserId(), userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "팔로우 상태 조회",
            description =
                    """
          특정 사용자 **userId**와의 **팔로우 관계 상태**를 조회합니다.

          - `isFollowing`: 내가 해당 유저를 팔로우 중인지
          - `isFollowedBy`: 해당 유저가 나를 팔로우하는지 (서로 팔로우 여부 확인에 유용)

          예시 요청:
          - `GET /api/users/42/follow-status`
          """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = FollowStatusDto.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "status-example",
                                                        value =
                                                                """
                      {
                        "isFollowing": true,
                        "isFollowedBy": false
                      }
                      """))),
                @ApiResponse(responseCode = "404", description = "해당 userId가 존재하지 않음")
            })
    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<FollowStatusDto> followStatus(
            @Parameter(description = "대상 사용자 ID", example = "42") @PathVariable Long userId) {
        return ResponseEntity.ok(friendsService.status(current.getUserId(), userId));
    }
}

package com.example.routie_be.domain.mypage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.routie_be.domain.mypage.dto.*;
import com.example.routie_be.domain.mypage.service.UsersService;
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

@Tag(name = "User API (MyPage)", description = """
        마이페이지 관련 사용자 API
        """)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    private final CurrentUserService current;

    @Operation(
            summary = "내 프로필 조회",
            description =
                    """
          현재 로그인한 사용자의 프로필 정보를 조회합니다.<br><br>
          - JWT 기반 인증이 완료된 사용자만 접근 가능<br>
          - 닉네임, 프로필 이미지, 가입일 등 기본 정보 반환
          """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "프로필 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserMeResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                      {
                        "id": 7,
                        "nickname": "루티",
                        "email": "routie@example.com",
                        "profileImage": "https://routie.s3.ap-northeast-2.amazonaws.com/profile/routie.png",
                        "friendDays": 42,
                        "badgeCount": 3
                      }
                      """)))
            })
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMyProfile() {
        return ResponseEntity.ok(usersService.getProfile(current.getUserId()));
    }

    @Operation(
            summary = "프로필 수정",
            description =
                    """
          내 프로필 정보를 수정합니다.<br><br>
          - 닉네임, 프로필 이미지 변경 가능<br>
          - 요청 Body로 `UserUpdateRequest` JSON 전달
          """,
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            description = "수정할 프로필 정보",
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema =
                                                    @Schema(
                                                            implementation =
                                                                    UserUpdateRequest.class),
                                            examples =
                                                    @ExampleObject(
                                                            value =
                                                                    """
                  {
                    "nickname": "루루",
                    "profileImage": "https://routie.s3.ap-northeast-2.amazonaws.com/profile/pickle.png"
                  }
                  """))),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "프로필 수정 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserMeResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                      {
                        "id": 7,
                        "nickname": "피클이",
                        "email": "routie@example.com",
                        "profileImage": "https://routie.s3.ap-northeast-2.amazonaws.com/profile/pickle.png",
                        "friendDays": 42,
                        "badgeCount": 3
                      }
                      """))),
                @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값 (예: 닉네임 중복)")
            })
    @PatchMapping("/me")
    public ResponseEntity<UserMeResponse> updateProfile(@RequestBody UserUpdateRequest dto) {
        return ResponseEntity.ok(usersService.updateProfile(current.getUserId(), dto));
    }

    @Operation(
            summary = "저장한 루트 목록 조회",
            description =
                    """
          내가 저장(북마크)한 루트 목록을 조회합니다.<br><br>
          - 페이징 파라미터 제공 (`page`, `size`)<br>
          - 응답은 `RouteSummary` 리스트로 반환
          """,
            parameters = {
                @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
                @Parameter(name = "size", description = "한 페이지당 조회 개수", example = "20")
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "저장한 루트 목록 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(
                                                                        implementation =
                                                                                RouteSummary
                                                                                        .class)),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                      [
                        {
                          "id": 101,
                          "title": "서울 도심 루트",
                          "thumbnail": "https://routie.s3.ap-northeast-2.amazonaws.com/route/thumb1.png",
                          "distance": 5.2,
                          "duration": 45,
                          "isSaved": true
                        },
                        {
                          "id": 205,
                          "title": "한강 자전거 루트",
                          "thumbnail": "https://routie.s3.ap-northeast-2.amazonaws.com/route/thumb2.png",
                          "distance": 12.8,
                          "duration": 90,
                          "isSaved": true
                        }
                      ]
                      """)))
            })
    @GetMapping("/me/saved")
    public ResponseEntity<List<RouteSummary>> getSavedRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(usersService.getSavedRoutes(current.getUserId(), page, size));
    }
}

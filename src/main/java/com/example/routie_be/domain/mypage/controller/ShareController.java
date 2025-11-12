package com.example.routie_be.domain.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.routie_be.domain.mypage.dto.ShareProfileResponse;
import com.example.routie_be.domain.mypage.service.ShareService;
import com.example.routie_be.global.common.CurrentUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Profile Share API", description = """
        사용자 프로필 공유 링크 발급 및 조회 API
        """)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final CurrentUserService current;

    @Operation(
            summary = "공유 링크 발급",
            description =
                    """
                로그인된 사용자가 자신의 프로필 공유용 **slug**를 발급받습니다.<br><br>
                - 본인 계정(userId)이 아닐 경우 403 Forbidden 반환<br>
                - slug는 랜덤 문자열로 발급되어 `/share/users/{slug}` 경로로 접근 가능합니다.
                """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "성공적으로 공유 링크를 발급했습니다.",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ShareProfileResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                    {
                                      "userId": 7,
                                      "slug": "abc123xy",
                                      "shareUrl": "https://routie.shop/share/users/abc123xy",
                                      "issuedAt": "2025-11-11T03:42:00"
                                    }
                                """))),
                @ApiResponse(responseCode = "403", description = "본인 계정이 아닌 경우 발급 불가")
            })
    @PostMapping("/users/{userId}/share")
    public ResponseEntity<ShareProfileResponse> issueSlug(
            @Parameter(description = "공유 링크를 발급할 사용자 ID (본인만 가능)", example = "7") @PathVariable
                    Long userId) {
        if (!userId.equals(current.getUserId())) {
            return ResponseEntity.status(403).build(); // 본인만 발급 가능
        }
        return ResponseEntity.ok(shareService.issueSlug(userId));
    }

    @Operation(
            summary = "슬러그로 유저 ID 조회",
            description =
                    """
                발급된 **slug**를 이용해 해당 유저의 ID를 조회합니다.<br><br>
                - 존재하지 않는 slug의 경우 404 Not Found 반환<br>
                - 성공 시 userId(Long)를 반환합니다.
                """,
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "유효한 slug로 사용자 ID 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(example = "7"),
                                        examples = @ExampleObject(value = "7"))),
                @ApiResponse(responseCode = "404", description = "해당 slug가 존재하지 않음")
            })
    @GetMapping("/share/users/{slug}")
    public ResponseEntity<Long> resolveSlug(
            @Parameter(description = "프로필 공유용 슬러그 문자열", example = "abc123xy") @PathVariable
                    String slug) {
        Long userId = shareService.resolveUser(slug);
        return (userId == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(userId);
    }
}

package com.example.routie_be.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiError", description = "공통 에러 응답 형식")
public record ApiError(
        @Schema(description = "HTTP 상태 코드", example = "400") int status,
        @Schema(description = "에러 메시지", example = "이미 팔로우 중입니다.") String message,
        @Schema(description = "추가 데이터 (없으면 null)", example = "null") Object data) {}

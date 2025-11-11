package com.example.routie_be.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 빌더 패턴으로 객체 생성 (데이터 전달 시 편리)
public class LoginResponse {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String accessToken; // 클라이언트가 이후 API 요청 시 사용할 토큰
    // private final String refreshToken; // (선택 사항: 토큰 재발급용)
}
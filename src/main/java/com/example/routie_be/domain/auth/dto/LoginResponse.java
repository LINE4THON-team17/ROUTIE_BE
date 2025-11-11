package com.example.routie_be.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String accessToken;
}

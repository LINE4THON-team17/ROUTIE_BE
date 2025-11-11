package com.example.routie_be.domain.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupResponse {
    private final Long userId;
    private final String email;
    private final String nickname;
}

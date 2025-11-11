package com.example.routie_be.domain.auth.controller;

import com.example.routie_be.domain.auth.dto.LoginRequest;
import com.example.routie_be.domain.auth.dto.LoginResponse;
import com.example.routie_be.domain.auth.dto.SignupRequest;
import com.example.routie_be.domain.auth.dto.SignupResponse;
import com.example.routie_be.domain.auth.service.AuthService;
import com.example.routie_be.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.signup(request);
            return ResponseEntity.status(201).body(
                    new ApiResponse<>(201, "회원가입 성공", response)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(
                    new ApiResponse<>(409, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    new ApiResponse<>(400, "요청 형식이 올바르지 않습니다.", null)
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(
                    new ApiResponse<>(200, "로그인 성공", response)
            );
        } catch (IllegalArgumentException e) {
            // 이메일이나 비밀번호 오류
            return ResponseEntity.status(401).body(
                    new ApiResponse<>(401, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    new ApiResponse<>(400, "로그인 요청 처리 중 오류 발생", null)
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        return ResponseEntity.ok(
                new ApiResponse<>(200, "로그아웃 완료 (JWT는 클라이언트에서 삭제)", null)
        );
    }
}
package com.example.routie_be.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.routie_be.domain.auth.dto.LoginRequest;
import com.example.routie_be.domain.auth.dto.LoginResponse;
import com.example.routie_be.domain.auth.dto.SignupRequest;
import com.example.routie_be.domain.auth.dto.SignupResponse;
import com.example.routie_be.domain.auth.service.AuthService;
import com.example.routie_be.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "회원가입, 로그인, 로그아웃 관련 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임을 입력받아 회원을 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.signup(request);
            return ResponseEntity.status(201).body(new ApiResponse<>(201, "회원가입 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(new ApiResponse<>(409, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(400, "요청 형식이 올바르지 않습니다.", null));
        }
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고, JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "로그인 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(401, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>(400, "로그인 요청 처리 중 오류 발생", null));
        }
    }

    @Operation(summary = "로그아웃", description = "JWT는 서버에서 무효화하지 않고, 클라이언트 측에서 삭제하도록 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        return ResponseEntity.ok(new ApiResponse<>(200, "로그아웃 완료 (JWT는 클라이언트에서 삭제)", null));
    }
}

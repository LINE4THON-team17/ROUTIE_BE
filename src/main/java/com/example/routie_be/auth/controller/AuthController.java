package com.example.routie_be.auth.controller;


import com.example.routie_be.auth.dto.LoginRequest;
import com.example.routie_be.auth.dto.LoginResponse;
import com.example.routie_be.auth.dto.SignupRequest;
import com.example.routie_be.auth.dto.SignupResponse;
import com.example.routie_be.auth.service.AuthService;
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
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {

        // "Bearer " 접두사 제거 (Access Token)
        String accessToken = bearerToken.substring(7);

        // 💡 AuthService.logout을 호출하지만, 이제 이 메서드는 아무 작업도 하지 않습니다.
        authService.logout(accessToken);

        // 클라이언트는 200 OK를 받고, 토큰을 로컬에서 삭제하게 됩니다.
        return ResponseEntity.ok().build();
    }
}
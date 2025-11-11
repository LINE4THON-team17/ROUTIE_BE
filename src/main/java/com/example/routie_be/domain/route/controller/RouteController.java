package com.example.routie_be.domain.route.controller;

import com.example.routie_be.global.common.ApiResponse;
import com.example.routie_be.domain.route.dto.RouteCreateRequest;
import com.example.routie_be.domain.route.dto.RouteData;
import com.example.routie_be.domain.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<ApiResponse<RouteData>> createRoute(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody RouteCreateRequest request
    ) {
        // 🚨 1. Null 체크 및 인증 실패 응답
        if (userId == null) {
            // userId가 null이면 JWT 인증은 통과했으나 Principal 주입이 실패했거나,
            // 인증 필터에서 Principal을 설정하지 못했음을 의미합니다.
            // 클라이언트에게는 401 UNAUTHORIZED로 응답하는 것이 적절합니다.
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "인증 정보(User ID)를 가져올 수 없습니다. 유효한 토큰인지 확인하세요.",
                            null
                    ));
        }

        // 2. userId가 정상적으로 전달되면 Service 호출
        ApiResponse<RouteData> response = routeService.createRoute(userId, request);

        // 3. 200 OK 응답 반환
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
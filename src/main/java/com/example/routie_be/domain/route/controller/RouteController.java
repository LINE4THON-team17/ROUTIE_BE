package com.example.routie_be.domain.route.controller;

import com.example.routie_be.domain.route.dto.RouteCreateRequest;
import com.example.routie_be.domain.route.dto.RouteData;
import com.example.routie_be.domain.route.service.RouteService;
import com.example.routie_be.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            "인증 정보(User ID)를 가져올 수 없습니다. 유효한 토큰인지 확인하세요.",
                            null
                    ));
        }
        ApiResponse<RouteData> response = routeService.createRoute(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

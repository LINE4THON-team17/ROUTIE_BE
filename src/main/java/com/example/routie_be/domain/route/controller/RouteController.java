package com.example.routie_be.domain.route.controller;

import com.example.routie_be.domain.route.dto.RouteCreateRequest;
import com.example.routie_be.domain.route.dto.RouteData;
import com.example.routie_be.domain.route.dto.RouteDetailDto;
import com.example.routie_be.domain.route.dto.RouteSummaryDto;
import com.example.routie_be.domain.route.service.RouteService;
import com.example.routie_be.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteSummaryDto>>> getRouteList() {

        List<RouteSummaryDto> routeList = routeService.getRouteList();

        if (routeList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "등록된 루트가 없습니다.",
                            null
                    ));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "루트 목록 조회 성공",
                        routeList
                )
        );
    }

    //루트 상세 보기
    @GetMapping("/{routeId}") // 💡 엔드포인트 경로 어노테이션 추가
    public ResponseEntity<ApiResponse<RouteDetailDto>> getRouteDetail(
            @PathVariable Long routeId
    ) {
        try {
            // Service 호출
            RouteDetailDto routeDetail = routeService.getRouteDetail(routeId);

            // 200 OK 응답 구성
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            "루트 상세 조회 성공",
                            routeDetail
                    )
            );
        } catch (IllegalArgumentException e) {
            // Service에서 던진 "해당 루트를 찾을 수 없습니다." 예외 처리 (404)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage(), // "해당 루트를 찾을 수 없습니다."
                            null
                    ));
        }
    }
}

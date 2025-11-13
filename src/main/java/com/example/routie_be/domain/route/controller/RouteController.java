package com.example.routie_be.domain.route.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.routie_be.domain.mypage.service.SavedRouteService;
import com.example.routie_be.domain.route.dto.RouteCreateRequest;
import com.example.routie_be.domain.route.dto.RouteData;
import com.example.routie_be.domain.route.dto.RouteDetailDto;
import com.example.routie_be.domain.route.dto.RouteSummaryDto;
import com.example.routie_be.domain.route.service.RouteService;
import com.example.routie_be.domain.route.service.S3Uploader;
import com.example.routie_be.global.common.ApiResponse;
import com.example.routie_be.global.common.CurrentUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routes")
@Tag(name = "Route", description = "루트 생성 및 조회 API")
public class RouteController {

    private final RouteService routeService;
    private final CurrentUserService currentUserService;
    private final S3Uploader s3Uploader; // ✅ 추가

    @Operation(
            summary = "루트 생성",
            description =
                    "회원이 방문한 장소 정보를 포함한 새로운 루트를 생성합니다. "
                            + "요청 본문에는 루트 제목, 대상, 키워드 목록, 방문 장소 리스트 등이 포함되어야 합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<RouteData>> createRoute(
            @Valid @RequestBody RouteCreateRequest request) {

        Long userId = currentUserService.getUserId();

        ApiResponse<RouteData> response = routeService.createRoute(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "루트 목록 조회",
            description = "등록된 모든 루트 목록을 조회합니다. " + "각 루트의 제목, 키워드, 등록일 등의 요약 정보가 포함됩니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteSummaryDto>>> getRouteList() {

        List<RouteSummaryDto> routeList = routeService.getRouteList();

        if (routeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "등록된 루트가 없습니다.", null));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(HttpStatus.OK.value(), "루트 목록 조회 성공", routeList));
    }

    @Operation(
            summary = "루트 상세 조회",
            description = "루트 ID를 이용해 특정 루트의 상세 정보를 조회합니다. " + "루트 내 포함된 장소 목록, 리뷰 등 세부 정보가 반환됩니다.")
    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<RouteDetailDto>> getRouteDetail(
            @Parameter(description = "조회할 루트의 ID", example = "1") @PathVariable Long routeId) {
        try {
            RouteDetailDto routeDetail = routeService.getRouteDetail(routeId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(HttpStatus.OK.value(), "루트 상세 조회 성공", routeDetail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }

    private final SavedRouteService savedRouteService;

    // 루트 저장
    @Operation(summary = "루트 저장", description = "다른 사용자의 루트를 내 저장 목록에 추가합니다.")
    @PostMapping("/{routeId}/save")
    public ResponseEntity<ApiResponse<String>> saveRoute(@PathVariable Long routeId) {
        Long userId = currentUserService.getUserId();
        ApiResponse<String> response = savedRouteService.saveRoute(userId, routeId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 루트 저장 취소
    @Operation(summary = "루트 저장 취소", description = "저장한 루트를 삭제합니다.")
    @DeleteMapping("/{routeId}/save")
    public ResponseEntity<ApiResponse<String>> unsaveRoute(@PathVariable Long routeId) {
        Long userId = currentUserService.getUserId();
        ApiResponse<String> response = savedRouteService.unsaveRoute(userId, routeId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "루트 삭제", description = "본인이 작성한 루트를 삭제합니다.")
    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponse<String>> deleteRoute(@PathVariable Long routeId) {
        Long userId = currentUserService.getUserId();
        ApiResponse<String> response = routeService.deleteRoute(userId, routeId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}

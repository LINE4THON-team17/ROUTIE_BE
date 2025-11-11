package com.example.routie_be.domain.map.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.routie_be.domain.map.dto.MapConfigDto;
import com.example.routie_be.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config")
@Tag(name = "MapConfig", description = "카카오 지도 설정 관련 API")
public class MapConfigController {

    @Value("${KAKAO_MAP_JS_KEY:TEST_KEY}")
    private String mapApiKey;

    @Operation(
            summary = "지도 설정 정보 조회",
            description = "카카오 지도 JS 키 및 기본 위도/경도 정보를 반환합니다."
    )
    @GetMapping("/map")
    public ResponseEntity<ApiResponse<MapConfigDto>> getMapConfig() {
        MapConfigDto config =
                MapConfigDto.builder()
                        .mapApiKey(mapApiKey)
                        .defaultLat(37.5665)   // 기본 중심 좌표 (서울시청)
                        .defaultLng(126.9780)
                        .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(HttpStatus.OK.value(), "지도 설정 정보 조회 성공", config));
    }
}

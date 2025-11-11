package com.example.routie_be.domain.map.controller;

import com.example.routie_be.domain.map.dto.MapConfigDto;
import com.example.routie_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config") // 설정 관련 엔드포인트
public class MapConfigController {

    // 💡 .env 파일에서 지도 API 키와 기본값을 읽어옵니다.
    @Value("${KAKAO_MAP_JS_KEY:TEST_KEY}")
    private String mapApiKey;

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<MapConfigDto>> getMapConfig() {
        MapConfigDto config = MapConfigDto.builder()
                .mapApiKey(mapApiKey)
                .defaultLat(37.5665) // 서울 시청 위도
                .defaultLng(126.9780) // 서울 시청 경도
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "지도 설정 정보 조회 성공",
                        config
                )
        );
    }
}
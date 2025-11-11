package com.example.routie_be.domain.map.controller;

import com.example.routie_be.domain.map.service.KakaoMapService;
import com.example.routie_be.domain.route.dto.PlaceSelectionDto;
import com.example.routie_be.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place/search")
@Tag(name = "PlaceSearch", description = "카카오 지도 장소 검색 API")
public class PlaceSearchController {

    private final KakaoMapService kakaoMapService;

    @Operation(
            summary = "장소 검색",
            description = "카카오 지도 API를 이용해 키워드 기반으로 장소를 검색합니다."
    )
    @GetMapping("/places")
    public ResponseEntity<ApiResponse<List<PlaceSelectionDto>>> searchPlaces(
            @Parameter(description = "검색할 키워드 (예: 카페, 홍대입구 등)", required = true)
            @RequestParam String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "검색 키워드는 필수입니다.",
                            null
                    ));
        }

        List<PlaceSelectionDto> results = kakaoMapService.searchPlaceByKeyword(keyword);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "장소 검색 결과 조회 성공",
                        results
                ));
    }
}
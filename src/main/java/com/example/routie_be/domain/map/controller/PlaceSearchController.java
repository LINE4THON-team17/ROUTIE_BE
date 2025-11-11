package com.example.routie_be.domain.map.controller;

import com.example.routie_be.domain.map.service.KakaoMapService;
import com.example.routie_be.domain.route.dto.PlaceSelectionDto;
import com.example.routie_be.global.common.ApiResponse;
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
@RequestMapping("/api/search") // 검색 엔드포인트 그룹
public class PlaceSearchController {

    private final KakaoMapService kakaoMapService;

    /**
     * GET /api/search/places?keyword={검색어}
     * 카카오 API를 통해 장소를 검색하고, 필요한 DTO 리스트로 반환합니다.
     */
    @GetMapping("/places")
    public ResponseEntity<ApiResponse<List<PlaceSelectionDto>>> searchPlaces(
            @RequestParam String keyword
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "검색 키워드는 필수입니다.", null));
        }

        // 카카오 API 호출 및 DTO 변환
        List<PlaceSelectionDto> results = kakaoMapService.searchPlaceByKeyword(keyword);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "장소 검색 결과 조회 성공",
                        results
                )
        );
    }
}
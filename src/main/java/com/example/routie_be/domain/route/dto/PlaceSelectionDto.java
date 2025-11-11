package com.example.routie_be.domain.route.dto;

import lombok.Builder;
import lombok.Getter;

//카카오 검색 API 결과 중, 루트 생성 시 필요한 정보만 담는 DTO

@Getter
@Builder
public class PlaceSelectionDto {
    private final String name;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final String category;
}
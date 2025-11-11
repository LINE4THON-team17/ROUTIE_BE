package com.example.routie_be.domain.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapConfigDto {
    private final String mapApiKey; // 지도 API 키
    private final Double defaultLat; // 기본 중심 위도 (예: 서울)
    private final Double defaultLng; // 기본 중심 경도 (예: 서울)
}

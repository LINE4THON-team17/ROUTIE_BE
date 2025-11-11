package com.example.routie_be.domain.route.dto;

import com.example.routie_be.domain.route.entity.Place;

import lombok.Builder;
import lombok.Getter;

// 장소 상세 조회
@Getter
@Builder
public class PlaceDetailDto {
    private final Long placeId;
    private final Integer order; // 순서
    private final String name;
    private final String category; // 카테고리 뱃지
    private final String address; // 주소
    private final Double latitude; // 지도 좌표
    private final Double longitude;
    private final String photoUrl; // 장소 사진
    private final String review; // 리뷰 내용

    public static PlaceDetailDto from(Place place) {
        return PlaceDetailDto.builder()
                .placeId(place.getPlaceId())
                .order(place.getOrder())
                .name(place.getName())
                .category(place.getCategory())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .photoUrl(place.getPhotoUrl())
                .review(place.getReview())
                .build();
    }
}

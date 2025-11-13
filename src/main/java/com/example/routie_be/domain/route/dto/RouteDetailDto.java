package com.example.routie_be.domain.route.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.routie_be.domain.route.entity.Route;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteDetailDto {
    private final Long routeId;
    private final String title; // 코스 이름
    private final Long userId; // 작성자 ID
    private final List<String> keywords;
    private final String visitedDate;
    private final List<PlaceDetailDto> places;

    public static RouteDetailDto from(Route route) {
        List<PlaceDetailDto> placeDetails =
                route.getPlaces().stream()
                        .map(PlaceDetailDto::from)
                        .sorted(Comparator.comparing(PlaceDetailDto::getOrder)) // 순서대로 정렬
                        .collect(Collectors.toList());

        List<String> keywordList = route.getKeywords().stream().collect(Collectors.toList());

        return RouteDetailDto.builder()
                .routeId(route.getRouteId())
                .userId(route.getUserId())
                .title(route.getTitle())
                .keywords(keywordList)
                .visitedDate(route.getVisitedDate())
                .places(placeDetails)
                .build();
    }
}

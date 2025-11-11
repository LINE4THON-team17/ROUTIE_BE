package com.example.routie_be.domain.route.dto;

import com.example.routie_be.domain.route.entity.Route;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class RouteSummaryDto {
    private final Long routeId;
    private final String title;
    private final String thumbnailUrl;
    private final List<String> keywords;
    private final String createdAt;

    public static RouteSummaryDto from(Route route) {
        String thumbnailUrl = null;
        if (route.getPlaces() != null && !route.getPlaces().isEmpty()) {
            thumbnailUrl =
                    route.getPlaces().stream().findFirst().map(p -> p.getPhotoUrl()).orElse(null);
        }

        List<String> keywordList = route.getKeywords().stream().collect(Collectors.toList());

        String locationSummary = "지역 정보 없음";
        if (route.getPlaces() != null && !route.getPlaces().isEmpty()) {
            String fullAddress =
                    route.getPlaces().stream().findFirst().map(p -> p.getAddress()).orElse("");
            String[] addressParts = fullAddress.split(" ");
            if (addressParts.length >= 2) {
                locationSummary = addressParts[1] + " 일대";
            } else if (addressParts.length == 1) {
                locationSummary = addressParts[0];
            }
        }

        return RouteSummaryDto.builder()
                .routeId(route.getRouteId())
                .title(route.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .keywords(keywordList)
                .createdAt(route.getCreatedAt().toString())
                .build();
    }
}

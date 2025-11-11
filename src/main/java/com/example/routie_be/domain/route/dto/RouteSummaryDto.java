package com.example.routie_be.domain.route.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.routie_be.domain.route.entity.Route;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteSummaryDto {
    private final Long routeId;
    private final String title;
    private final String thumbnailUrl;
    private final List<String> keywords; // 클라이언트에게는 List로 제공
    private final String createdAt;

    public static RouteSummaryDto from(Route route) {
        String thumbnailUrl = null;
        if (route.getPlaces() != null && !route.getPlaces().isEmpty()) {
            // 장소는 Set이지만, Stream으로 첫 번째 요소를 찾습니다. (순서는 보장 안됨)
            thumbnailUrl =
                    route.getPlaces().stream().findFirst().map(p -> p.getPhotoUrl()).orElse(null);
        }

        // 💡 Set<String>을 List<String>으로 변환
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
                .keywords(keywordList) // List로 제공
                .createdAt(route.getCreatedAt().toString())
                .build();
    }
}

package com.example.routie_be.domain.route.service;

import com.example.routie_be.domain.route.dto.*;
import com.example.routie_be.domain.route.entity.Place;
import com.example.routie_be.domain.route.entity.Route;
import com.example.routie_be.domain.route.repository.RouteRepository;
import com.example.routie_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public ApiResponse<RouteData> createRoute(Long userId, RouteCreateRequest request) {
        Set<String> keywordSet = request.getKeywords().stream().collect(Collectors.toSet());

        Route newRoute =
                Route.builder()
                        .userId(userId)
                        .title(request.getTitle())
                        .target(request.getTarget())
                        .keywords(keywordSet) // 💡 Set<String> 타입으로 전달
                        .visitedDate(request.getVisitedDate())
                        .build();

        for (PlaceDto placeDto : request.getPlaces()) {
            Place place =
                    Place.builder()
                            .order(placeDto.getOrder())
                            .name(placeDto.getName())
                            .category(placeDto.getCategory())
                            .address(placeDto.getAddress())
                            .latitude(placeDto.getLatitude())
                            .longitude(placeDto.getLongitude())
                            .photoUrl(placeDto.getPhotoUrl())
                            .review(placeDto.getReview())
                            .build();

            newRoute.addPlace(place);
        }

        Route savedRoute = routeRepository.save(newRoute);

        String formattedCreatedAt =
                savedRoute.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        String redirectUrl = baseUrl + "/routes/" + savedRoute.getRouteId();

        RouteData routeData =
                RouteData.builder()
                        .routeId(savedRoute.getRouteId())
                        .createdAt(formattedCreatedAt)
                        .redirectUrl(redirectUrl)
                        .build();

        return new ApiResponse<>(200, "루트 생성 성공", routeData);
    }

    @Transactional(readOnly = true)
    public List<RouteSummaryDto> getRouteList() {
        List<Route> routes = routeRepository.findAllWithPlacesAndKeywords();

        return routes.stream().map(RouteSummaryDto::from).collect(Collectors.toList());
    }

    public RouteDetailDto getRouteDetail(Long routeId) {
        Route route =
                routeRepository
                        .findByIdWithDetails(routeId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 루트를 찾을 수 없습니다. ID: " + routeId));
        return RouteDetailDto.from(route);
    }
}

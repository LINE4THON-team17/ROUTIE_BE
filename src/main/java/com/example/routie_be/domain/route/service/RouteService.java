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

        // 1. DTO에서 List<String> keywords를 Set<String>으로 변환 (엔티티 타입에 맞춤)
        Set<String> keywordSet = request.getKeywords().stream().collect(Collectors.toSet());

        Route newRoute = Route.builder()
                .userId(userId)
                .title(request.getTitle())
                .target(request.getTarget())
                .keywords(keywordSet) // 💡 Set<String> 타입으로 전달
                .visitedDate(request.getVisitedDate())
                .build();

        // 2. Place DTO 리스트를 Place 엔티티로 변환하고 Route에 연결
        for (PlaceDto placeDto : request.getPlaces()) {
            Place place = Place.builder()
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

        // 3. DB 저장
        Route savedRoute = routeRepository.save(newRoute);

        String formattedCreatedAt = savedRoute.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        String redirectUrl = baseUrl + "/routes/" + savedRoute.getRouteId();

        RouteData routeData = RouteData.builder()
                .routeId(savedRoute.getRouteId())
                .createdAt(formattedCreatedAt)
                .redirectUrl(redirectUrl)
                .build();

        return new ApiResponse<>(200, "루트 생성 성공", routeData);
    }

    @Transactional(readOnly = true)
    public List<RouteSummaryDto> getRouteList() {
        // 💡 Fetch Join 쿼리 사용 (RouteRepository에서 findAllWithPlacesAndKeywords 호출)
        // 이 쿼리는 Set을 Fetch Join하므로 MultipleBagFetchException이 해결됩니다.
        List<Route> routes = routeRepository.findAllWithPlacesAndKeywords();

        // DTO로 변환
        return routes.stream()
                .map(RouteSummaryDto::from)
                .collect(Collectors.toList());
    }

    public RouteDetailDto getRouteDetail(Long routeId) {

        // 1. Repository에서 Fetch Join을 통해 모든 관련 엔티티를 조회
        Route route = routeRepository.findByIdWithDetails(routeId)
                // 2. 루트를 찾지 못할 경우 예외 처리 (404 Not Found에 해당)
                .orElseThrow(() -> new IllegalArgumentException("해당 루트를 찾을 수 없습니다. ID: " + routeId));

        // 3. 엔티티를 상세 DTO로 변환하여 반환
        return RouteDetailDto.from(route);
    }
}
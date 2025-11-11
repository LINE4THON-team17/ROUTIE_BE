package com.example.routie_be.domain.route.service;

import com.example.routie_be.domain.route.dto.PlaceDto;
import com.example.routie_be.domain.route.dto.RouteCreateRequest;
import com.example.routie_be.domain.route.dto.RouteData;
import com.example.routie_be.domain.route.entity.Place;
import com.example.routie_be.domain.route.entity.Route;
import com.example.routie_be.domain.route.repository.RouteRepository;
import com.example.routie_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public ApiResponse<RouteData> createRoute(Long userId, RouteCreateRequest request) {

        // 1. DTO를 Route 엔티티로 변환 및 사용자 ID 연결
        // (Controller에서 userId null 체크가 이루어진다고 가정)
        Route newRoute = Route.builder()
                .userId(userId)
                .title(request.getTitle())
                .target(request.getTarget())
                .keywords(request.getKeywords())
                .visitedDate(request.getVisitedDate())
                .build();

        // 2. Place DTO 리스트를 Place 엔티티로 변환하고 Route에 연결
        for (PlaceDto placeDto : request.getPlaces()) {
            Place place = Place.builder()
                    // place_order 컬럼명 문제로 인해 DB에 integer로 저장됨
                    .order(placeDto.getOrder())
                    .name(placeDto.getName())
                    .category(placeDto.getCategory())
                    .address(placeDto.getAddress())
                    .latitude(placeDto.getLatitude())
                    .longitude(placeDto.getLongitude())
                    .photoUrl(placeDto.getPhotoUrl())
                    .review(placeDto.getReview())
                    .build();

            // Cascade 설정에 의해 Place는 Route와 함께 저장됩니다.
            newRoute.addPlace(place);
        }

        // 3. DB 저장
        Route savedRoute = routeRepository.save(newRoute);

        // 💡 시간 포맷 오류 수정: ISO_INSTANT -> ISO_DATE_TIME
        String formattedCreatedAt = savedRoute.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);

        // 4. 응답 DTO 구성
        String redirectUrl = baseUrl + "/routes/" + savedRoute.getRouteId();

        RouteData routeData = RouteData.builder()
                .routeId(savedRoute.getRouteId())
                .createdAt(formattedCreatedAt)
                .redirectUrl(redirectUrl)
                .build();

        return new ApiResponse<>(200, "루트 생성 성공", routeData);
    }
}
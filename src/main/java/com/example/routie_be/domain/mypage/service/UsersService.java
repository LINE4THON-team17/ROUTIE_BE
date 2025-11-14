package com.example.routie_be.domain.mypage.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.auth.entity.User;
import com.example.routie_be.domain.auth.repository.UserRepository;
import com.example.routie_be.domain.mypage.dto.RouteSummary;
import com.example.routie_be.domain.mypage.dto.UserMeResponse;
import com.example.routie_be.domain.mypage.dto.UserUpdateRequest;
import com.example.routie_be.domain.mypage.entity.MypageUser;
import com.example.routie_be.domain.mypage.entity.UserSavedRoute;
import com.example.routie_be.domain.mypage.repository.FollowRepo;
import com.example.routie_be.domain.mypage.repository.SavedRouteRepo;
import com.example.routie_be.domain.mypage.repository.UserRepo;
import com.example.routie_be.domain.route.entity.Route;
import com.example.routie_be.domain.route.repository.RouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UserRepo userRepo;
    private final SavedRouteRepo savedRouteRepo;
    private final FollowRepo followRepo;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;

    public UserMeResponse getProfile(Long userId) {
        MypageUser u =
                userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        long routesCount = routeRepository.countByUserId(userId);
        long savedCount = savedRouteRepo.countByUserId(userId);
        long friendsCount = followRepo.countByFolloweeId(userId);

        return new UserMeResponse(
                u.getId(),
                u.getNickname(),
                u.getProfileImageUrl(),
                routesCount,
                savedCount,
                friendsCount);
    }

    @Transactional
    public UserMeResponse updateProfile(Long userId, UserUpdateRequest dto) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.nickname() != null
                && !dto.nickname().isBlank()
                && !dto.nickname().equals(user.getNickname())
                && userRepository.existsByNickname(dto.nickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        user.updateProfile(dto.nickname(), dto.profileImageUrl());

        long routesCount = 0L;
        long savedCount = savedRouteRepo.countByUserId(userId);
        long friendsCount = followRepo.countByFolloweeId(userId);

        return new UserMeResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                routesCount,
                savedCount,
                friendsCount);
    }

    public List<RouteSummary> getSavedRoutes(Long userId, int page, int size) {
        Page<UserSavedRoute> p =
                savedRouteRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        return p.stream()
                .map(
                        saved ->
                                routeRepository
                                        .findById(saved.getRouteId())
                                        .map(
                                                route ->
                                                        new RouteSummary(
                                                                route.getRouteId(),
                                                                route.getTitle(), // ✅ 실제 루트 타이틀
                                                                saved.getCreatedAt()))
                                        .orElse(null)) // 삭제된 루트면 null
                .filter(Objects::nonNull) // null 제거
                .toList();
    }

    public List<RouteSummary> getMyRoutes(Long userId, int page, int size) {

        Page<Route> routes =
                routeRepository.findByUserIdOrderByCreatedAtDesc(
                        userId,
                        PageRequest.of(page, size)
                );

        return routes.stream()
                .map(route ->
                        new RouteSummary(
                                route.getRouteId(),
                                route.getTitle(),
                                route.getCreatedAt()
                        )
                ).toList();
    }
}

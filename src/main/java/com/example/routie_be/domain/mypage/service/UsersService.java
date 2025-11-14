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
import com.example.routie_be.domain.mypage.dto.UserProfileResponse;
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

    /** 내 프로필 조회 */
    public UserMeResponse getProfile(Long userId) {
        MypageUser u =
                userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        long routesCount = routeRepository.countByUserId(userId);
        long savedCount = savedRouteRepo.countByUserId(userId);
        long friendsCount = followRepo.countByFollowerId(userId);

        return new UserMeResponse(
                u.getId(),
                u.getNickname(),
                u.getProfileImageUrl(),
                routesCount,
                savedCount,
                friendsCount);
    }

    /** 다른 유저 프로필 조회 (프로필 화면) viewerId = 현재 로그인한 나 targetId = 조회 대상 유저 */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long viewerId, Long targetId) {
        MypageUser u =
                userRepo.findById(targetId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        long routesCount = routeRepository.countByUserId(targetId);
        long friendsCount = followRepo.countByFollowerId(targetId);

        boolean isFriend = followRepo.existsByFollowerIdAndFolloweeId(viewerId, targetId);

        return new UserProfileResponse(
                u.getId(),
                u.getNickname(),
                u.getProfileImageUrl(),
                routesCount,
                friendsCount,
                isFriend);
    }

    /** 내 프로필 수정 */
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
        long friendsCount = followRepo.countByFollowerId(userId);

        return new UserMeResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                routesCount,
                savedCount,
                friendsCount);
    }

    /** 내가 저장한 루트 목록 조회 */
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
                                                                route.getTitle(),
                                                                saved.getCreatedAt()))
                                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public List<RouteSummary> getMyRoutes(Long userId, int page, int size) {

        Page<Route> routes =
                routeRepository.findByUserIdOrderByCreatedAtDesc(
                        userId, PageRequest.of(page, size));

        return routes.stream()
                .map(
                        route ->
                                new RouteSummary(
                                        route.getRouteId(), route.getTitle(), route.getCreatedAt()))
                .toList();
    }
}

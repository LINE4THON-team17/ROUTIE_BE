package com.example.routie_be.domain.mypage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.mypage.dto.*;
import com.example.routie_be.domain.mypage.entity.User;
import com.example.routie_be.domain.mypage.entity.UserSavedRoute;
import com.example.routie_be.domain.mypage.repository.FollowRepo;
import com.example.routie_be.domain.mypage.repository.SavedRouteRepo;
import com.example.routie_be.domain.mypage.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UserRepo userRepo;
    private final SavedRouteRepo savedRouteRepo;
    private final FollowRepo followRepo;

    public UserMeResponse getProfile(Long userId) {
        User u =
                userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        long routesCount = 0L; // TODO: Route 테이블 연동 후 수정
        // Saved 개수는 레포 카운트 메서드 사용(권장). 없다면 Page#getTotalElements로 대체.
        long savedCount = savedRouteRepo.countByUserId(userId);
        long friendsCount = followRepo.countByFolloweeId(userId);

        return new UserMeResponse(
                u.getId(),
                u.getName(),
                u.getProfileImageUrl(),
                routesCount,
                savedCount,
                friendsCount);
    }

    @Transactional
    public UserMeResponse updateProfile(Long userId, UserUpdateRequest dto) {
        User u =
                userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.name() != null && !dto.name().isBlank()) {
            u.setName(dto.name());
        }
        if (dto.profileImageUrl() != null && !dto.profileImageUrl().isBlank()) {
            u.setProfileImageUrl(dto.profileImageUrl());
        }

        long routesCount = 0L;
        long savedCount = savedRouteRepo.countByUserId(userId);
        long friendsCount = followRepo.countByFolloweeId(userId);

        return new UserMeResponse(
                u.getId(),
                u.getName(),
                u.getProfileImageUrl(),
                routesCount,
                savedCount,
                friendsCount);
    }

    public List<RouteSummary> getSavedRoutes(Long userId, int page, int size) {
        Page<UserSavedRoute> p =
                savedRouteRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        // TODO: routes 연동 시 실제 타이틀 매핑
        return p.map(r -> new RouteSummary(r.getRouteId(), "루트 제목(추후 routes 연동)", r.getCreatedAt()))
                .getContent();
    }
}
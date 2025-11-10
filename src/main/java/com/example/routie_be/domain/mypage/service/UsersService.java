package com.example.routie_be.domain.mypage.service;

import com.example.routie_be.domain.mypage.dto.*;
import com.example.routie_be.domain.mypage.entity.User;
import com.example.routie_be.domain.mypage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {
  private final UserRepo userRepo;
  private final SavedRouteRepo savedRouteRepo;
  private final FollowRepo followRepo;

  public UserMeResponse getProfile(Long userId) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    long routesCount = 0; // TODO: Route 테이블 연동 후 수정
    long savedCount = savedRouteRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 1)).getTotalElements();
    long friendsCount = followRepo.countByFollowerId(userId);

    return new UserMeResponse(
        user.getId(),
        user.getName(),
        user.getProfileImageUrl(),
        routesCount,
        savedCount,
        friendsCount
    );
  }

  public UserMeResponse updateProfile(Long userId, UserUpdateRequest dto) {
    User user = userRepo.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    if (dto.name() != null) user.setName(dto.name());
    if (dto.profileImageUrl() != null) user.setProfileImageUrl(dto.profileImageUrl());
    userRepo.save(user);

    return getProfile(userId);
  }

  public List<RouteSummary> getSavedRoutes(Long userId, int page, int size) {
    // TODO: routes 팀 연동 시 RouteSummaryMapper 구현
    return List.of();
  }
}
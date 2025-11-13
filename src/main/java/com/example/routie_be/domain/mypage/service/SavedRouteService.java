package com.example.routie_be.domain.mypage.service;

import com.example.routie_be.domain.mypage.entity.UserSavedRoute;
import com.example.routie_be.domain.mypage.repository.SavedRouteRepo;
import com.example.routie_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavedRouteService {

    private final SavedRouteRepo savedRouteRepo;

    // 루트 저장
    public ApiResponse<String> saveRoute(Long userId, Long routeId) {
        if (savedRouteRepo.existsByUserIdAndRouteId(userId, routeId)) {
            return new ApiResponse<>(HttpStatus.CONFLICT.value(), "이미 저장된 루트입니다.", null);
        }

        UserSavedRoute newSaved = new UserSavedRoute(userId, routeId);
        savedRouteRepo.save(newSaved);
        return new ApiResponse<>(HttpStatus.OK.value(), "루트 저장 성공", null);
    }

    // 루트 저장 취소
    public ApiResponse<String> unsaveRoute(Long userId, Long routeId) {
        if (!savedRouteRepo.existsByUserIdAndRouteId(userId, routeId)) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "저장되지 않은 루트입니다.", null);
        }

        savedRouteRepo
                .findAll()
                .stream()
                .filter(s -> s.getUserId().equals(userId) && s.getRouteId().equals(routeId))
                .findFirst()
                .ifPresent(savedRouteRepo::delete);

        return new ApiResponse<>(HttpStatus.OK.value(), "루트 저장 취소 성공", null);
    }
}

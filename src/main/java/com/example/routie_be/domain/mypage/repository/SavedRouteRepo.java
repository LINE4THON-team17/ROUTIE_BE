package com.example.routie_be.domain.mypage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.UserSavedRoute;

public interface SavedRouteRepo extends JpaRepository<UserSavedRoute, Long> {
    // 페이징 조회
    Page<UserSavedRoute> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 전체 조회
    List<UserSavedRoute> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 존재 여부
    boolean existsByUserIdAndRouteId(Long userId, Long routeId);

    // 개수
    long countByUserId(Long userId);
}

package com.example.routie_be.domain.mypage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.UserSavedRoute;

public interface SavedRouteRepo extends JpaRepository<UserSavedRoute, Long> {
    Page<UserSavedRoute> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndRouteId(Long userId, Long routeId);
}

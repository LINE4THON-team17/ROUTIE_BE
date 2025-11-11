package com.example.routie_be.domain.mypage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.UserProfileShare;

public interface ShareRepo extends JpaRepository<UserProfileShare, Long> {
    Optional<UserProfileShare> findBySlug(String slug);

    Optional<UserProfileShare> findByUserId(Long userId);

    boolean existsBySlug(String slug);
}

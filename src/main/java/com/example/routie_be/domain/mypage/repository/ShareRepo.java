package com.example.routie_be.domain.mypage.repository;

import com.example.routie_be.domain.mypage.entity.UserProfileShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareRepo extends JpaRepository<UserProfileShare, Long> {
  Optional<UserProfileShare> findBySlug(String slug);
  Optional<UserProfileShare> findByUserId(Long userId);
  boolean existsByUserId(Long userId);
}
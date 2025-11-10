package com.example.routie_be.domain.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.UserBadge;

public interface UserBadgeRepo extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUserId(Long userId);
}

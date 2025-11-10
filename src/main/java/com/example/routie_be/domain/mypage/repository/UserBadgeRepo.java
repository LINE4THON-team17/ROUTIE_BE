package com.example.routie_be.domain.mypage.repository;

import com.example.routie_be.domain.mypage.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepo extends JpaRepository<UserBadge, Long> {
  List<UserBadge> findByUserId(Long userId);
}
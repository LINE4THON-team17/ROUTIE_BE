package com.example.routie_be.domain.mypage.repository;

import com.example.routie_be.domain.mypage.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepo extends JpaRepository<Badge, Long> {
}
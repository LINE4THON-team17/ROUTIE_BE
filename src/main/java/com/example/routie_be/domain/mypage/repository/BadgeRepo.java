package com.example.routie_be.domain.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.Badge;

public interface BadgeRepo extends JpaRepository<Badge, Long> {}

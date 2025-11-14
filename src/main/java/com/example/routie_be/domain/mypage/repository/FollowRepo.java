package com.example.routie_be.domain.mypage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.UserFollow;

public interface FollowRepo extends JpaRepository<UserFollow, Long> {
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    Optional<UserFollow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    List<UserFollow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);

    long countByFollowerId(Long followerId);
}

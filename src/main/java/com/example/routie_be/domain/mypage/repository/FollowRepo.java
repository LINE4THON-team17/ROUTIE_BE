package com.example.routie_be.domain.mypage.repository;

import com.example.routie_be.domain.mypage.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepo extends JpaRepository<UserFollow, Long> {
  boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
  void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
  List<UserFollow> findByFollowerId(Long followerId);
  long countByFollowerId(Long followerId);
}
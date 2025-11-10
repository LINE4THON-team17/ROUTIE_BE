package com.example.routie_be.domain.mypage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_follow", uniqueConstraints = {
    @UniqueConstraint(name = "uk_follow", columnNames = {"followerId", "followeeId"})
})
public class UserFollow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long followerId;

  @Column(nullable = false)
  private Long followeeId;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  protected UserFollow() {}

  public UserFollow(Long followerId, Long followeeId) {
    this.followerId = followerId;
    this.followeeId = followeeId;
  }

  public Long getId() { return id; }
  public Long getFollowerId() { return followerId; }
  public Long getFolloweeId() { return followeeId; }
  public LocalDateTime getCreatedAt() { return createdAt; }
}
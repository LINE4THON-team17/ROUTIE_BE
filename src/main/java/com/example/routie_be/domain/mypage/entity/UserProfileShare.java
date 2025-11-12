package com.example.routie_be.domain.mypage.entity;

import java.time.LocalDateTime;

import com.example.routie_be.global.entity.BaseTimeEntity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_profile_share",
        indexes = @Index(name = "idx_slug", columnList = "slug", unique = true))
public class UserProfileShare extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column private LocalDateTime lastAccessedAt;

    protected UserProfileShare() {}

    public UserProfileShare(Long userId, String slug) {
        this.userId = userId;
        this.slug = slug;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSlug() {
        return slug;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}

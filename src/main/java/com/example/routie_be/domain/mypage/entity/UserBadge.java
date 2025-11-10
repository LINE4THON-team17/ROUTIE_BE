package com.example.routie_be.domain.mypage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_badge",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_badge",
                    columnNames = {"userId", "badgeId"})
        })
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long badgeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime grantedAt;

    protected UserBadge() {}

    public UserBadge(Long userId, Long badgeId) {
        this.userId = userId;
        this.badgeId = badgeId;
    }

    @PrePersist
    protected void onCreate() {
        this.grantedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getBadgeId() {
        return badgeId;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }
}

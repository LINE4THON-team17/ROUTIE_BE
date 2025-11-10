package com.example.routie_be.domain.mypage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_saved_route",
        indexes = {@Index(name = "idx_usr_user_created", columnList = "userId,createdAt")})
public class UserSavedRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long routeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected UserSavedRoute() {}

    public UserSavedRoute(Long userId, Long routeId) {
        this.userId = userId;
        this.routeId = routeId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

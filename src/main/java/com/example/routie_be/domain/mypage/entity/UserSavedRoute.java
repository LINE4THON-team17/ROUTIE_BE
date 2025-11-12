package com.example.routie_be.domain.mypage.entity;

import com.example.routie_be.global.entity.BaseTimeEntity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_saved_route",
        indexes = @Index(name = "idx_usr_user_created", columnList = "userId,createdAt"))
public class UserSavedRoute extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long routeId;

    protected UserSavedRoute() {}

    public UserSavedRoute(Long userId, Long routeId) {
        this.userId = userId;
        this.routeId = routeId;
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
}

package com.example.routie_be.domain.route.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    // JWT에서 추출하여 저장될 사용자 ID (외래 키 역할)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 200)
    private String target;

    @ElementCollection
    @CollectionTable(name = "route_keyword", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    @Column(name = "visited_date")
    private String visitedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 1:N 관계: Route가 삭제되면 Place도 함께 삭제 (Route가 주인)
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places = new ArrayList<>();

    @Builder
    public Route(Long userId, String title, String target, List<String> keywords, String visitedDate) {
        this.userId = userId;
        this.title = title;
        this.target = target;
        this.keywords = keywords;
        this.visitedDate = visitedDate;
        this.createdAt = LocalDateTime.now();
    }

    // 편의 메서드: Place를 추가할 때 Route와의 연관 관계를 설정
    public void addPlace(Place place) {
        this.places.add(place);
        place.setRoute(this);
    }
}
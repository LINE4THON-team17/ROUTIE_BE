package com.example.routie_be.domain.route.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 200)
    private String target;

    @ElementCollection
    @CollectionTable(name = "route_keyword", joinColumns = @JoinColumn(name = "route_id"))
    @Column(name = "keyword")
    private Set<String> keywords = new HashSet<>();

    @Column(name = "visited_date")
    private String visitedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Place> places = new HashSet<>();

    @Builder
    public Route(
            Long userId, String title, String target, Set<String> keywords, String visitedDate) {
        this.userId = userId;
        this.title = title;
        this.target = target;
        this.keywords = keywords;
        this.visitedDate = visitedDate;
        this.createdAt = LocalDateTime.now();
    }

    public void addPlace(Place place) {
        this.places.add(place);
        place.setRoute(this);
    }
}

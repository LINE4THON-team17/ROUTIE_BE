package com.example.routie_be.domain.route.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    // Route와의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "place_order", nullable = false)
    private Integer order; // 순서 (1, 2, ...)

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(length = 255)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Lob private String review;

    @Builder
    public Place(
            Integer order,
            String name,
            String category,
            String address,
            Double latitude,
            Double longitude,
            String photoUrl,
            String review) {
        this.order = order;
        this.name = name;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
        this.review = review;
    }

    // Route 연관 관계 설정을 위한 Setter
    public void setRoute(Route route) {
        this.route = route;
    }
}

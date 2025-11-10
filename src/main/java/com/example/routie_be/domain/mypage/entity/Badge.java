package com.example.routie_be.domain.mypage.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "badge")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    private Integer minRoutes;
    private Integer maxRoutes;

    protected Badge() {}

    public Badge(String name, String description, Integer minRoutes, Integer maxRoutes) {
        this.name = name;
        this.description = description;
        this.minRoutes = minRoutes;
        this.maxRoutes = maxRoutes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMinRoutes() {
        return minRoutes;
    }

    public Integer getMaxRoutes() {
        return maxRoutes;
    }
}

package com.example.routie_be.domain.route.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.routie_be.domain.route.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query(
            "SELECT r FROM Route r "
                    + "LEFT JOIN FETCH r.places p "
                    + "LEFT JOIN FETCH r.keywords k "
                    + "ORDER BY r.createdAt DESC")
    List<Route> findAllWithPlacesAndKeywords();

    @Query(
            "SELECT r FROM Route r LEFT JOIN FETCH r.places p LEFT JOIN FETCH r.keywords k WHERE r.routeId = :routeId")
    Optional<Route> findByIdWithDetails(@Param("routeId") Long routeId);

    long countByUserId(Long userId);
}

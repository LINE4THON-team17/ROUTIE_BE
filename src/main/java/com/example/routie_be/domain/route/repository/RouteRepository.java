package com.example.routie_be.domain.route.repository;

import com.example.routie_be.domain.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

// Route 엔티티와 Long 타입의 ID를 사용하는 Repository 인터페이스
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Spring Data JPA가 기본적인 CRUD 메서드를 자동으로 제공합니다.
}
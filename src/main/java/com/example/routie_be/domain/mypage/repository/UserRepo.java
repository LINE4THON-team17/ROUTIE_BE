package com.example.routie_be.domain.mypage.repository;

import com.example.routie_be.domain.mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}
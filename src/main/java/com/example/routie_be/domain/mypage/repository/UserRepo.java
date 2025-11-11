package com.example.routie_be.domain.mypage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.routie_be.domain.mypage.entity.MypageUser;

public interface UserRepo extends JpaRepository<MypageUser, Long> {
    Optional<MypageUser> findByEmail(String email);

    List<MypageUser> findByIdIn(List<Long> ids);
}

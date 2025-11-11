package com.example.routie_be.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
// 테이블명
public class User {

    // getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname; // UI에서의 아이디에 해당하는 필드

    // 기본 생성자
    public User() {}

    // 생성자
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

}

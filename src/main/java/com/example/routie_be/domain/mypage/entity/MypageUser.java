package com.example.routie_be.domain.mypage.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.*;

@Entity
@Table(name = "mypage_user_view")
@Immutable
public class MypageUser {

    @Id private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    protected MypageUser() {}

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}

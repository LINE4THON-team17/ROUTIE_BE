package com.example.routie_be.domain.mypage.dto;

/**
 * 친구(타 사용자) 프로필 조회 응답 DTO.
 *
 * <p>- id: 조회 대상 유저의 ID<br>
 * - nickname: 닉네임<br>
 * - profileImageUrl: 프로필 이미지 URL<br>
 * - routesCount: 해당 유저가 만든 루트 개수<br>
 * - friendsCount: 해당 유저가 팔로우한 사람 수<br>
 * - isFriend: 현재 로그인한 내가 이 유저를 팔로우 중인지 여부
 */
public record UserProfileResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        long routesCount,
        long friendsCount,
        boolean isFriend) {}

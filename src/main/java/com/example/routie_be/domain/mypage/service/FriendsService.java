package com.example.routie_be.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.routie_be.domain.mypage.dto.*;
import com.example.routie_be.domain.mypage.entity.UserFollow;
import com.example.routie_be.domain.mypage.repository.FollowRepo;
import com.example.routie_be.domain.mypage.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final FollowRepo followRepo;
    private final UserRepo userRepo;

    public List<FriendDto> list(Long me) {
        return followRepo.findByFollowerId(me).stream()
                .map(f -> userRepo.findById(f.getFolloweeId()).orElseThrow())
                .map(u -> new FriendDto(u.getId(), u.getName(), u.getProfileImageUrl()))
                .toList();
    }

    public void follow(Long me, Long target) {
        if (!followRepo.existsByFollowerIdAndFolloweeId(me, target)) {
            followRepo.save(new UserFollow(me, target));
        }
    }

    public void unfollow(Long me, Long target) {
        followRepo.deleteByFollowerIdAndFolloweeId(me, target);
    }

    public FollowStatusDto status(Long me, Long target) {
        return new FollowStatusDto(followRepo.existsByFollowerIdAndFolloweeId(me, target));
    }
}

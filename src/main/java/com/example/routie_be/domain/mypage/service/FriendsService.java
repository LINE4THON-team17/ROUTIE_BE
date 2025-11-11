package com.example.routie_be.domain.mypage.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.mypage.dto.FollowStatusDto;
import com.example.routie_be.domain.mypage.dto.FriendDto;
import com.example.routie_be.domain.mypage.entity.User;
import com.example.routie_be.domain.mypage.entity.UserFollow;
import com.example.routie_be.domain.mypage.repository.FollowRepo;
import com.example.routie_be.domain.mypage.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendsService {

    private final FollowRepo followRepo;
    private final UserRepo userRepo;

    public List<FriendDto> list(Long myId) {
        List<UserFollow> follows = followRepo.findByFollowerIdOrderByCreatedAtDesc(myId);
        if (follows.isEmpty()) return List.of();

        List<Long> ids = follows.stream().map(UserFollow::getFolloweeId).distinct().toList();

        Map<Long, User> userMap =
                userRepo.findByIdIn(ids).stream().collect(Collectors.toMap(User::getId, u -> u));

        return follows.stream()
                .map(UserFollow::getFolloweeId)
                .map(userMap::get)
                .filter(Objects::nonNull) // 삭제/비활성 사용자 대비
                .map(u -> new FriendDto(u.getId(), u.getName(), u.getProfileImageUrl()))
                .toList();
    }

    @Transactional
    public void follow(Long myId, Long targetId) {
        if (Objects.equals(myId, targetId)) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }
        if (followRepo.existsByFollowerIdAndFolloweeId(myId, targetId)) {
            return;
        }
        followRepo.save(new UserFollow(myId, targetId));
    }

    @Transactional
    public void unfollow(Long myId, Long targetId) {
        followRepo.findByFollowerIdAndFolloweeId(myId, targetId).ifPresent(followRepo::delete);
    }

    public FollowStatusDto status(Long myId, Long targetId) {
        boolean iFollow = followRepo.existsByFollowerIdAndFolloweeId(myId, targetId);
        boolean followMe = followRepo.existsByFollowerIdAndFolloweeId(targetId, myId);
        return new FollowStatusDto(iFollow, followMe);
    }
}

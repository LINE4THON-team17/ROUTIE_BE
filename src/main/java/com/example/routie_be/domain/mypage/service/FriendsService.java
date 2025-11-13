package com.example.routie_be.domain.mypage.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.routie_be.domain.mypage.dto.FollowStatusDto;
import com.example.routie_be.domain.mypage.dto.FriendDto;
import com.example.routie_be.domain.mypage.entity.MypageUser;
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

    private void assertTargetExists(Long targetId) {
        if (!userRepo.existsById(targetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 userId가 존재하지 않습니다.");
        }
    }

    public List<FriendDto> list(Long myId) {
        List<UserFollow> follows = followRepo.findByFollowerIdOrderByCreatedAtDesc(myId);
        if (follows.isEmpty()) return List.of();

        List<Long> ids = follows.stream().map(UserFollow::getFolloweeId).distinct().toList();

        Map<Long, MypageUser> userMap =
                userRepo.findByIdIn(ids).stream()
                        .collect(Collectors.toMap(MypageUser::getId, u -> u));

        return follows.stream()
                .map(UserFollow::getFolloweeId)
                .map(userMap::get)
                .filter(Objects::nonNull)
                .map(u -> new FriendDto(u.getId(), u.getNickname(), u.getProfileImageUrl()))
                .toList();
    }

    @Transactional
    public void follow(Long myId, Long targetId) {
        if (Objects.equals(myId, targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신은 팔로우할 수 없습니다.");
        }

        assertTargetExists(targetId);

        if (followRepo.existsByFollowerIdAndFolloweeId(myId, targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 팔로우 중입니다.");
        }

        followRepo.save(new UserFollow(myId, targetId));
    }

    @Transactional
    public void unfollow(Long myId, Long targetId) {
        assertTargetExists(targetId);

        UserFollow rel =
                followRepo
                        .findByFollowerIdAndFolloweeId(myId, targetId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST, "팔로우 중이 아닙니다."));

        followRepo.delete(rel);
    }

    public FollowStatusDto status(Long myId, Long targetId) {
        assertTargetExists(targetId);

        boolean iFollow = followRepo.existsByFollowerIdAndFolloweeId(myId, targetId);
        boolean followMe = followRepo.existsByFollowerIdAndFolloweeId(targetId, myId);
        return new FollowStatusDto(iFollow, followMe);
    }
}

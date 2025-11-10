package com.example.routie_be.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.routie_be.domain.mypage.dto.BadgeDto;
import com.example.routie_be.domain.mypage.repository.BadgeRepo;
import com.example.routie_be.domain.mypage.repository.UserBadgeRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepo badgeRepo;
    private final UserBadgeRepo userBadgeRepo;

    public List<BadgeDto> myBadges(Long userId) {
        return userBadgeRepo.findByUserId(userId).stream()
                .map(ub -> badgeRepo.findById(ub.getBadgeId()).orElseThrow())
                .map(b -> new BadgeDto(b.getId(), b.getName(), b.getDescription()))
                .toList();
    }

    public List<BadgeDto> definitions() {
        return badgeRepo.findAll().stream()
                .map(b -> new BadgeDto(b.getId(), b.getName(), b.getDescription()))
                .toList();
    }
}

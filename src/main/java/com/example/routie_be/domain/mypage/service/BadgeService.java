package com.example.routie_be.domain.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.mypage.dto.BadgeDto;
import com.example.routie_be.domain.mypage.entity.Badge;
import com.example.routie_be.domain.mypage.entity.UserBadge;
import com.example.routie_be.domain.mypage.repository.BadgeRepo;
import com.example.routie_be.domain.mypage.repository.SavedRouteRepo;
import com.example.routie_be.domain.mypage.repository.UserBadgeRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepo badgeRepo;
    private final UserBadgeRepo userBadgeRepo;
    private final SavedRouteRepo savedRouteRepo;

    /** 배지 정의 전체 목록 (정렬) */
    public List<BadgeDto> definitions() {
        return badgeRepo.findAllByOrderByMinRoutesAsc().stream().map(this::toDto).toList();
    }

    /**
     * 내 배지 목록: 1) 현재 저장 루트 개수로 획득 가능 배지를 계산 2) 아직 미발급이면 user_badge에 자동 발급(insert) 3) 최종적으로 내가 가진(또는
     * 방금 발급된) 배지 목록 반환
     */
    @Transactional
    public List<BadgeDto> myBadges(Long userId) {
        long routeCount = savedRouteRepo.countByUserId(userId);

        // 1) 획득 가능 배지 정의
        List<Badge> eligible =
                badgeRepo.findAllByOrderByMinRoutesAsc().stream()
                        .filter(
                                b ->
                                        routeCount >= b.getMinRoutes()
                                                && routeCount <= b.getMaxRoutes())
                        .toList();

        // 2) 아직 없는 배지는 user_badge에 자동 발급
        for (Badge b : eligible) {
            boolean hasIt = userBadgeRepo.existsByUserIdAndBadgeId(userId, b.getId());
            if (!hasIt) {
                userBadgeRepo.save(new UserBadge(userId, b.getId()));
            }
        }

        // 3) 최종 보유 배지 목록(정의에서 필터)
        return eligible.stream().map(this::toDto).toList();
    }

    private BadgeDto toDto(Badge b) {
        return new BadgeDto(
                b.getId(), b.getName(), b.getDescription(), b.getMinRoutes(), b.getMaxRoutes());
    }
}
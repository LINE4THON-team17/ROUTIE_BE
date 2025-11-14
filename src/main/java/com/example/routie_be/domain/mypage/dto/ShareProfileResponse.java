package com.example.routie_be.domain.mypage.dto;

import java.time.LocalDateTime;

/**
 * 사용자가 자신의 프로필을 외부에 공유할 때 발급되는 정보 응답 DTO.
 *
 * <p>구성 요소:
 * <ul>
 *   <li><b>userId</b> - 공유를 발급한 사용자 ID</li>
 *   <li><b>slug</b> - 프로필 공유용 고유 식별자 (임의 문자열)</li>
 *   <li><b>shareUrl</b> - 사용자가 공유 가능한 최종 URL</li>
 *   <li><b>issuedAt</b> - 공유 링크가 발급된 시간</li>
 * </ul>
 *
 * <p>이 DTO는 단순 조회용이며, 프로필 공유 생성 API에서 사용됨.</p>
 */
public record ShareProfileResponse(
    Long userId,
    String slug,
    String shareUrl,
    LocalDateTime issuedAt
) {}
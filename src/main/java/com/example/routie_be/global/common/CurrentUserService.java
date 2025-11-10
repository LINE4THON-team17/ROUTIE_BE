package com.example.routie_be.global.common;

import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
  // JWT 붙기 전까지는 임시 userId=1 하드코딩
  public Long getUserId() {
    return 1L;
  }
}
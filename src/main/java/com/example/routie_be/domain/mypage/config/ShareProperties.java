package com.example.routie_be.domain.mypage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.share")
public record ShareProperties(String baseUrl, String pathTemplate, int slugLength) {}

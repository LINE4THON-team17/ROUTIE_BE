package com.example.routie_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.routie_be.domain.mypage.config.ShareProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(ShareProperties.class)
public class RoutieBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoutieBeApplication.class, args);
    }
}

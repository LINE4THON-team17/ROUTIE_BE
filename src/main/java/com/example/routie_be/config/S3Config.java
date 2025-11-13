package com.example.routie_be.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Value("${cloud.aws.region.static:}")
    private String region;

    @Bean
    @ConditionalOnProperty(name = "cloud.aws.credentials.access-key")
    public S3Client s3Client() {

        if (!StringUtils.hasText(accessKey)
                || !StringUtils.hasText(secretKey)
                || !StringUtils.hasText(region)) {

            log.warn("AWS S3 비활성화: cloud.aws.* 설정값이 없습니다.");
            return null;
        }

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }
}

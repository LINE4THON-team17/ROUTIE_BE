package com.example.routie_be.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

    private static final Logger log = LoggerFactory.getLogger(S3Config.class);

    @Value("${cloud.aws.credentials.access-key:${CLOUD_AWS_CREDENTIALS_ACCESS_KEY:}}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:${CLOUD_AWS_CREDENTIALS_SECRET_KEY:}}")
    private String secretKey;

    @Value("${cloud.aws.region.static:${CLOUD_AWS_REGION_STATIC:ap-northeast-2}}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {

        if (!StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey)) {
            log.warn("AWS S3 비활성화: access-key/secret-key 설정이 없습니다.");
            throw new IllegalStateException("AWS 자격 증명 없음");
        }

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }
}

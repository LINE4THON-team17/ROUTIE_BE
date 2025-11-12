package com.example.routie_be.domain.mypage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShareProperties {

    private final String baseUrl;
    private final String pathTemplate;
    private final int slugLength;

    public ShareProperties(
            @Value("${share.base-url:${SHARE_BASE_URL:https://routie.shop}}") String baseUrl,
            @Value("${share.path-template:${SHARE_PATH_TEMPLATE:/share/users/%s}}")
                    String pathTemplate,
            @Value("${share.slug-length:${SHARE_SLUG_LENGTH:8}}") int slugLength) {
        this.baseUrl = baseUrl;
        this.pathTemplate = pathTemplate;
        this.slugLength = slugLength;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String pathTemplate() {
        return pathTemplate;
    }

    public int slugLength() {
        return slugLength;
    }
}

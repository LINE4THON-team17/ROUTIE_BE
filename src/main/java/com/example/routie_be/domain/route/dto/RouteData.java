package com.example.routie_be.domain.route.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteData {

    private Long routeId;
    private String createdAt;
    private String redirectUrl;

}
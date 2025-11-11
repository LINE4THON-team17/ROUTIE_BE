package com.example.routie_be.domain.route.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteCreateRequest {

    @NotBlank(message = "제목(title)은 필수 입력값입니다.")
    private String title;

    private String target;

    @NotEmpty(message = "키워드(keywords)는 최소 하나 이상이어야 합니다.")
    private List<String> keywords;

    private String visitedDate;

    @Valid
    @NotEmpty(message = "장소(places)는 최소 하나 이상이어야 합니다.")
    @Size(min = 1, message = "장소(places)는 최소 하나 이상이어야 합니다.")
    private List<PlaceDto> places;
}

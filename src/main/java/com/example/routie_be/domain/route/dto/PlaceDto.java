package com.example.routie_be.domain.route.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceDto {

    @NotNull(message = "장소 순서는 필수입니다.")
    private Integer order;

    @NotBlank(message = "장소 이름은 필수입니다.")
    private String name;

    private String category;
    private String address;

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    private String photoUrl;

    private String review; // 장소당 단일 리뷰


}

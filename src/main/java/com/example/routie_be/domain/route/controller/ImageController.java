package com.example.routie_be.domain.route.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.routie_be.domain.route.service.ImageService;
import com.example.routie_be.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Image", description = "이미지 업로드 API")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 업로드", description = "단일 이미지 파일을 S3에 업로드하고 URL을 반환합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = imageService.uploadImage(file);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "이미지 업로드 성공", imageUrl));
    }
}

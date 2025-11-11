package com.example.routie_be.global.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
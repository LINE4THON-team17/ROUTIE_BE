package com.example.routie_be.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {
    // getter & setter
    private String email;
    private String password;
    private String nickname;

}

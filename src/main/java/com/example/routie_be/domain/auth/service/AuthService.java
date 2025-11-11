package com.example.routie_be.domain.auth.service;

import com.example.routie_be.domain.auth.dto.LoginRequest;
import com.example.routie_be.domain.auth.dto.LoginResponse;
import com.example.routie_be.domain.auth.dto.SignupRequest;
import com.example.routie_be.domain.auth.dto.SignupResponse;
import com.example.routie_be.domain.auth.entity.User;
import com.example.routie_be.domain.auth.repository.UserRepository;
import com.example.routie_be.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public SignupResponse signup(SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );

        userRepository.save(user);

        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .build();
    }
}

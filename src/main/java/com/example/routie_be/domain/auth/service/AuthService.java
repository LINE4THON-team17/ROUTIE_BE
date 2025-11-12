package com.example.routie_be.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.routie_be.domain.auth.dto.LoginRequest;
import com.example.routie_be.domain.auth.dto.LoginResponse;
import com.example.routie_be.domain.auth.dto.SignupRequest;
import com.example.routie_be.domain.auth.dto.SignupResponse;
import com.example.routie_be.domain.auth.entity.User;
import com.example.routie_be.domain.auth.repository.UserRepository;
import com.example.routie_be.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        final String email = request.getEmail().trim().toLowerCase();
        final String nickname = request.getNickname().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = new User(email, passwordEncoder.encode(request.getPassword()), nickname);
        userRepository.save(user);

        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        final String email = request.getEmail().trim().toLowerCase();

        User user =
                userRepository
                        .findByEmail(email)
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

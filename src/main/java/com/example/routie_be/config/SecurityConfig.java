package com.example.routie_be.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.routie_be.security.JwtAuthenticationFilter;
import com.example.routie_be.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔒 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 🌐 CORS 설정
                .cors(
                        cors ->
                                cors.configurationSource(
                                        request -> {
                                            CorsConfiguration config = new CorsConfiguration();
                                            config.setAllowedOriginPatterns(List.of("*"));
                                            config.setAllowedMethods(
                                                    List.of(
                                                            "GET", "POST", "PUT", "DELETE",
                                                            "OPTIONS"));
                                            config.setAllowedHeaders(List.of("*"));
                                            config.setAllowCredentials(true);
                                            return config;
                                        }))

                // 🧩 세션 사용 안 함 (JWT 방식)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🔐 요청별 접근 권한 설정
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()

                                        // ✅ Swagger & Actuator 허용
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/actuator/**",
                                                "/swagger-ui.html",
                                                "/swagger-resources/**",
                                                "/webjars/**")
                                        .permitAll()

                                        // ✅ 인증 없이 접근 가능한 경로
                                        .requestMatchers("/api/auth/signup", "/api/auth/login")
                                        .permitAll()

                                        // ✅ 인증 필요한 요청
                                        .requestMatchers(HttpMethod.POST, "/routes")
                                        .authenticated()

                                        // ❌ 나머지는 기본적으로 인증 필요
                                        .anyRequest()
                                        .authenticated())

                // 🧱 formLogin / httpBasic 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 🧩 JWT 필터 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔑 비밀번호 암호화 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

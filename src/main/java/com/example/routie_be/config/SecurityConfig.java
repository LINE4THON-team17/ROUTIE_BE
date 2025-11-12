package com.example.routie_be.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(
                        cors ->
                                cors.configurationSource(
                                        request -> {
                                            CorsConfiguration c = new CorsConfiguration();
                                            c.setAllowedOriginPatterns(
                                                    List.of(
                                                            "https://routie.shop",
                                                            "http://localhost:*",
                                                            "*"));
                                            c.setAllowedMethods(
                                                    List.of(
                                                            "GET", "POST", "PUT", "PATCH", "DELETE",
                                                            "OPTIONS"));
                                            c.setAllowedHeaders(
                                                    List.of(
                                                            "Authorization",
                                                            "Content-Type",
                                                            "Accept",
                                                            "Origin"));
                                            c.setAllowCredentials(false);
                                            return c;
                                        }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/actuator/**",
                                                "/error")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                                        .permitAll()
                                        .requestMatchers("/api/auth/signup", "/api/auth/login")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/share/users/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/users/*/share")
                                        .authenticated()
                                        .anyRequest()
                                        .authenticated())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

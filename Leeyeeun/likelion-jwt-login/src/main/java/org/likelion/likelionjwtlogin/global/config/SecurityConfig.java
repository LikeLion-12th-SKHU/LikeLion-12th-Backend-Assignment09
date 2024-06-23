package org.likelion.likelionjwtlogin.global.config;

import lombok.RequiredArgsConstructor;
import org.likelion.likelionjwtlogin.global.jwt.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 스프링 설정
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor // final 필드에 대해서 생성자를 자동으로 생성
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter; // JWT 인증 필터를 주입받음
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() // "/members/**" 경로는 인증 없이도 접급을 허용
                        .anyRequest().authenticated() // 나머지의 요청들은 인증을 요구
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않고 JWT를 사용하여 관리
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
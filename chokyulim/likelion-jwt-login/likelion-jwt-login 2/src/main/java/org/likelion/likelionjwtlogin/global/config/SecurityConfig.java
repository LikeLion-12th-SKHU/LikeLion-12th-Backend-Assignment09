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

@Configuration // 이 클래스가 스프링의 설정 클래스를 나타내는 것을 의미
@EnableWebSecurity // 스프링 시큐리티 설정을 활성화
@RequiredArgsConstructor // final 필드들에 대한 생성자를 자동으로 생성 (lombok 기능)
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter; // JwtAuthorizationFilter를 주입받아 사용

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능을 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() // /members/** 경로에 대한 요청은 인증 없이 접근 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 전에 JwtAuthorizationFilter를 추가
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않고 JWT를 사용하여 상태 관리
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 다양한 패스워드 인코딩 방식을 지원하는 PasswordEncoder를 생성
    }
}

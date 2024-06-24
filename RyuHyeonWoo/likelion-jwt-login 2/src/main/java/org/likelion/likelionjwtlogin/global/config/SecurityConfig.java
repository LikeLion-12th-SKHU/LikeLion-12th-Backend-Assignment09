package org.likelion.likelionjwtlogin.global.config;

import lombok.RequiredArgsConstructor;
import org.likelion.likelionjwtlogin.global.jwt.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 해당 클래스가 스프링 설정 클래스임을 의미
@EnableWebSecurity // 스프링 security 활성화
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동 생성
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter; // JwtAuthorizationFilter 필터를 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() // /members/ 경로를 통한 요청 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 후 접근 가능
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 전에 JWT 인증 필터 추가
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 정책을 STATELESS로 설정하여 토큰 기반 인증을 사용하도록 설정
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // 비밀번호 인코더를 빈으로 등록하여  비밀번호 암호화

        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 비밀번호 생성
    }
}

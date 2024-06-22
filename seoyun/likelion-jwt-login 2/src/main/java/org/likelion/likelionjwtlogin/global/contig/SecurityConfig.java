package org.likelion.likelionjwtlogin.global.contig;

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

@Configuration //하나 이상의 bean메서드 가지고 있어 spring컨테이너가 메서드 관리, 빈으로 등록하는 것을 나타냄
@EnableWebSecurity //spring security 활성화(웹 보안 설정 제공)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter; //1차적으로 로그인 처리하는 필터

    //fliterchain 메서드는 spring security의 securityfliterchain설정함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() //주소가 /members/** 를 제외한 모든 요청에 대해 인증된 사용자만 접근 가능

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) //다양한 보안 요구사항 처리
                .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))//세션 정책을  stateless으로 설정, 서버가 세션을 생성하지 않고 토큰 기반 인증을 사용하도록 설정하는 것을 의미함
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

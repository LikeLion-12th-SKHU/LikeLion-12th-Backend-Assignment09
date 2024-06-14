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

// 이 클래스가 하나 이상의 @Bean 메서드를 가지고 있어 스프링 컨테이너가 해당 메서드들을 관리하고, 빈으로 등록한다는 것을 나타낸다.
@Configuration
// Spring Security를 활성화한다. 이 어노테이션은 기본적인 웹 보안 설정을 제공한다. 이 어노테이션이 포함된 클래스를 통해 세부적인 보안 설정을 커스터마이징할 수 있다.
@EnableWebSecurity
// final이 붙은 필드의 생성자를 자동 생성해준다.
@RequiredArgsConstructor
public class SecurityConfig {
    // JWT 인증 필터를 주입받는다.
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    // 해당 메서드가 반환하는 객체를 스프링 컨테이너에 빈으로 등록한다.
    @Bean
    // filterChain 메서드는 Spring Security의 SecurityFilterChain을 설정한다. 이는 HTTP 요청에 대해 어떤 보안 필터가 적용될 지를 정의한다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // csrf 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // /members/** 경로를 제외한 모든 요청에 대해 인증된 사용자만 접근 가능
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() // /members/** 경로는 모든 사용자에게 허용
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증된 사용자만 접근 가능
                )
                // UsernamePasswordAuthenticationFilter 앞에 커스텀 필터인 jwtAuthorizationFilter를 추가하여 JWT 를 검증하고 인증 정보를 설정한다.
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                // 세션관리에 대한 설정. 세션 정책을 STATELESS로 설정하며, 이는 서버가 세션을 생성하지 않고 토큰 기반 인증을 사용하도록 설정한다.
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 빌드하여 반환
                .build();
    }

    // 해당 메서드가 반환하는 객체를 스프링 컨테이너에 빈으로 등록한다.
    @Bean
    // 비밀번호 인코더를 빈으로 등록하여 비밀번호를 암호화하는데 사용한다.
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

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

// 이 클래스가 하나 이상의 @Bean 메서드를 가지고 있어 Spring 컨테이너가 해당 메서드들을 관리하고,
// 빈으로 등록한다는 것을 나타내는 어노테이션이다.
@Configuration
// Spring Secuirty를 활성화 합니다. 이 어노테이션은 기본적인 웹 보안 설정을 제공한다.
// 이 어노테이션은이 포함된 클래스를 통해 세부적인 보안 설정을 커스터마이징 할 수 있다.
@EnableWebSecurity
// final 필드에 대한 생성자를 자동으로 생성해준다.
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    // Spring Security의 fillterChain을 설정하는 메서드. 이는 HTTP 요청에 대한 어떤 보안 필터가 적용될지 정의한다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable) // csrf를 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() // 주소가 /members/**를 제외한 모든 요청에 대해 인증된 사용자만 접근 가능하다.
                        .anyRequest().authenticated()// 개발자가 허락한 멤버스에 대해서 허용 해주는 기능이다.
                )
                // UsernamePasswordAuthenticationFilter 앞에 커스텀 필터인 jwtAuthorizationFilter를 추가하여 JWT를 검증하고 인증 정보를 설정한다.
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // 특정 필터를 추가하여 다양한 보안 요구사항을 처리합니다.
                //
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))// 서버가 세션을 사용하지 않고, 토큰 기반 인증을 사용하도록 설정한다.
                .build(); // SecurityFillterChain 객체를 빌드하여 반환
    }

    @Bean
    // passwordEncoder를 위한 PasswordEncoder 빈을 생성한다.
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

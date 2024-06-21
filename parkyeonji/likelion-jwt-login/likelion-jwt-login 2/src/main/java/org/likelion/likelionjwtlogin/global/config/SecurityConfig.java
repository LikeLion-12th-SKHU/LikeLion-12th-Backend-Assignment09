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

import java.security.Security;

@Configuration //이 클래스가 하나 이상의 @Bean 메서드를 가지고 있어 Spring 컨테이너가 해당 메서드들을 관리하고, 빈으로 등록한다는 것을 의미.
@EnableWebSecurity //Spring Security 활성화. 기본적인 웹 보안 설정 제공.
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    //filterChain: Spring Security의 SecurityFilterChain 설정. HTTP 요청에 대해 어떤 보안 필터가 적용될 지를 정의.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화. CsrfFilter : GET요청을 제외한 상태를 변화시킬 수 있는 POST, PUT, DELETE 요청으로부터 보호.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll()
                        .anyRequest().authenticated()
                ) //주소가 /members/** 를 제외한 모든 요청에 대해 인증된 사용자만 접근 가능.
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                /*
                * HTTP 요청이 UsernamePasswordAuthenticationFilter에 도달하기 전에 jwtAuthorizationFilter가 먼저 실행됨.
                * UsernamePasswordAuthenticationFilter: 기본적으로 사용자 이름과 비밀번호를 사용한 인증을 처리하는 필터.
                * 그러나 JWT를 사용한 인증에서는 토큰을 사용하므로, 이 필터 앞에 커스텀 필터인 jwtAuthorizationFilter를 추가하여 JWT를 검증하고 인증 정보를 설정.
                */

                /*
                * HTTP 요청이 들어옴 > 클라이언트가 서버에 HTTP 요청을 보냄 > 요청이 필터 체인을 따라 이동
                * > jwtAuthorizationFilter가 먼저 실행됨 > JWT 검증 (요청의 헤더에서 JWT 추출, 유효한 토큰인지 확인, 유효하다면 Authentication 객체 생성)
                * > SecurityContext 설정 (생성된 객체를 SecurityContextHolder에 설정하여 이후의 보안 체인에서 사용자가 인증된 것으로 인식되도록 함)
                * > (검증이 완료되면) UsernamePasswordAuthenticationFilter로 이동(이미 인증이 완료되었기 때문에 추가적인 인증 수행X)
                * > 요청 처리 > 최종적으로 컨트롤러에 도달
                */

                //세션 관리에 대한 설정
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 정책을 STATELESS로 설정. 토큰 기반 인증 사용하도록 설정하는 것.(세션 생성X)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    } // 비밀번호를 인코딩하여 암호화.
}

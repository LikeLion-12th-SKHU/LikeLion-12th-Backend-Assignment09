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

@Configuration
@EnableWebSecurity //스프링 시큐리티 활성화(e.g. 필터 체인 생성), 웹 보안 설정 구성
@RequiredArgsConstructor
public class SecurityConfig {
    //JwtAuthorizationFilter : 1차적으로 로그인 처리하는 필터
    //username과 password로 인증 과정을 거친 후 응답 헤더에 JWT를 담아 반환해주는 역할
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    //SecurityFilterChain filterChain(HttpSecurity http) : HttpSecurity 설정하는 객체 생성
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Spring Security의 각종 설정은 HttpSecurity로 구현한다
        return http
                //csrf(Cross Site Request Forgery, 사이트간 위조 요청) : 웹사이트 공격 방법 중 하나를 예방하는 기능
                //disable인 이유 : REST API 서버 기준으로는 비활성화 하는 것이 좋다 함
                .csrf(AbstractHttpConfigurer::disable)
                //리소스 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        //.requestMatchers("/members/**") : 특정 리소스("/members/**")에 대한 권한을
                        //.permitAll() : 인증절차 없이 허용한다
                        .requestMatchers("/members/**").permitAll()
                        //.anyRequest() : 특정 권한을 가진 사용자에게 접근 가능한 리소스 설정한 뒤, 그외 >>나머지 리소스들<<
                        //.authenticated() : >>나머지 리소스들<<은 무조건 인증을 완료해야 한다.(if else 느낌이다)
                        .anyRequest().authenticated()
                )
                //.addFilterBefore : 필터 등록
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                //Session 로그인 방식이 아닌 JWT 로그인 방식이므로 SessionCreationPolicy를 STATELESS로
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    //PasswordEncoder passwordEncoder() : DB 저장 시의 password Encoding 설정하는 객체 생성
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

package org.likelion.likelionjwtlogin.global.config;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
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

@Configuration //스프링이 돌아가는데에 있 설정을 관리하는 객체 그리고 Bean을 수동으로 등록
@EnableWebSecurity //웹 보안 설정을 제공한다
@RequiredArgsConstructor //생성자 주입
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter; //jwt 인증필터

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //보안필터 체인설정
        return http
                .csrf(AbstractHttpConfigurer::disable) //csrf:사이트간 요청 위조 , csrf 보호를 비활성화 시킴
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/members/**").permitAll() //"/members/**" 경로는 접근을 허용시킨다
                        .anyRequest().authenticated() //나머지는 다 인증절차를 거쳐야한다
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) //jwt인증필터 추가
                .sessionManagement(sessionManagement -> sessionManagement   //세션 관리 정책. 토큰 관리하겠다.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션을 사용하지 않음
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();  // 비밀번호 암호화
    }
}
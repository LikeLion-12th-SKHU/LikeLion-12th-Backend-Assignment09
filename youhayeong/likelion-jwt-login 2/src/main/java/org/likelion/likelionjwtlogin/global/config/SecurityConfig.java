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

@Configuration  // 하나 이상의 @Bean 메서드를 가지고 있어 spring 컨테이너가 해당 메서드들을 관리하고 빈으로 등록
@EnableWebSecurity  // spring security 활성화, 웹 보안 설정 제공
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;    // 사용자 정의 jwt 인증 필터

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {    // HTTP 요청에 대한 어떤 필터가 적용될지 정의
        return http
                .csrf(AbstractHttpConfigurer::disable)  // csrf 비활성화, csrf: 사이트 간 요청 위조로 웹 보안 취약점 중 하나, 서버에 인증 정보를 저장하지 않는 jwt 방식이기에 불필요한 csrf 코드 작성 필요 x => 비활성화
                .authorizeHttpRequests(authorize -> authorize   // 주소가 /members/**를 제외한 모든 요청에 대해 인증된 사용자만 접근 가능
                        .requestMatchers("/members/**").permitAll() // /members/**로 시작하는 주소의 접근은 모두 허용
                        .anyRequest().authenticated()       // 그 외의 주소에 대해서는 인증 요구
                )
                // 지정된 필터 앞에 커스텀 필터(jwtAuthorizationFilter)를 추가 = addFilterBefore
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)   //  username ~ : 사용자 이름과 비밀번호를 사용한 인증을 처리하는 필터, jwtauthorationfilter: jwt를 검증하고 인증 정보 설정하는 커스텀 필터(이게 먼저 실행됨
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))    // 세션 정책을 stateless로 설정하여, 서버가 세션을 생성하지 않고 토큰 기반 인증을 사용하도록 설정 = jwt 방식
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();  // 패스워드 암호화
    }
}

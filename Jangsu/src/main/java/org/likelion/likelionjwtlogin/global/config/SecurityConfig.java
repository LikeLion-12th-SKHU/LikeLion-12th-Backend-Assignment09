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

// 하나 이상의 @Bean 메서드를 가지고 있어 Spring 컨데이너가 해당 메서드들을 관리, 빈으로 등록하는 것을 의미한다.
@Configuration
// Spring Security 를 활성화하는데, 기본적인 웹 보안 설정을 제공하고 포함된 클래스를 통해 세부적인 보안 설정을 커스터마이징 할 수 있다.
@EnableWebSecurity
// REST API 컨트롤러에 대한 예외처리 어드바이스임을 나타내는 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean       // 개발자가 직접 제어가 불가능한 외부 라이브러리등을 Bean 으로 만들 때 사용      콩
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // csrf 를 비활성화한다.
        // Non-browser clients 인 경우 CSRF 를 비활성화 해도 좋다고 한다.
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                                // 주소가 "/members/**" 를 제외한 모든 요청에 대해 인증된 사용자만 접근 가능.
                                .requestMatchers("/members/**")
                                .permitAll().anyRequest().authenticated()
                )
                // 특정 필터를 추가하여 보안 요구사항을 처리한다.
                // addFilterBefore : 지정된 필터 앞에 커스텀 필터를 추가한다.
                // sessionManagement : 세션 관리에 대한 전략들을 설정할 수 있다.
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy
                        (SessionCreationPolicy.STATELESS))
                // 세션 정책을 STATELESS 로 설정하며 이는 서버가 세션을 생성하지 않고 토큰 기반 인증을 사용하도록 설정하는 것을 의미함.
                .build();
        /*
        < 동작 방식 101 >
            1. UsernamePasswordAuthenticationFilter.class 가 도달하기 전에 jwtAuthorizationFilter 가 먼저 실행

            2. JWT 를 검증한다.
                jwtAuthorizationFilter 는 요청의 헤더에서 JWT 를 추출하고 검증하여 유효한지 확인한다.
                유효하다면 토큰에서 사용자 정보를 추출하여 Authentication 객체를 생성한다.

            3. SecurityContext 를 설정한다.
                생성된 Authentication 객체를 SecurityContextHolder 에 설정하여, 이후 보안 체인에서 사용자가 인증된 것으로 설정함.

            4. 다음 필터로 이동
                JWT 검증이 완료되면 요청은 필터 체인을 따라 이동하여, 다음 필터인 UsernamePasswordAuthenticationFilter 로 넘어간다.
                그러나 이미 인증이 완료되었기 때문에 해당 필터는 추가적인 인증을 수행하지 않는다.

            5. 마지막 요청을 처리한다.
                요청은 컨트롤러에 도달하여, 필요한 리소스를 제공하거나 다른 비즈니스 로직을 수행한다.
         */
    }
    @Bean   // 개발자가 직접 제어가 불가능한 외부 라이브러리등을 Bean 으로 만들 때 사용. 상동      콩콩
    public PasswordEncoder passwordEncoder() {
        // 비밀번호를 암호화하하여 반환한다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }
}
// CSRF : 공격자가 인증된 브라우저에 저장된 쿠키의 세션 정보를 활용하여 웹 서버에 사용자가 의도치 않는 요청을 전달하는 것.
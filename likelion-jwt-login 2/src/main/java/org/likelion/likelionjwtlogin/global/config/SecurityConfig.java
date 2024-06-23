package org.likelion.likelionjwtlogin.global.config;

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

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // 기본적 security를 사용할 수 있게 해주는 어노테이션, 기본적인 웹보안 설정을 하게해준다.
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthorizationFilter jwtAuthorizationFilter; //jwt 인증필터를 주입.

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{ //어떤 필터들을 사용할 것인지 그 필터들의 체이닝 순서등을 작성한다.
		return http
			.csrf(AbstractHttpConfigurer::disable) // 기본 csrf관련 config 제거
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/members/**").permitAll() // 만약 merbers와 관련한 요청이면 인증없이 들어오는걸 허가한다.
				.anyRequest().authenticated()	//그외에 모든 요청은 인증절차를 거쳐야한다.
			)
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) //뒤에있는 UsernamePasswordAuthentication filter를 거치기 전에 jwt인증 필터를 먼저 거치게한다.
			.sessionManagement(sessionMangement -> sessionMangement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션관리 설정으로 STATELESS로 설정해주면 세션을 생성하지 않고 토큰 기반으로 사용한다.
			.build();

	}

	@Bean
	public PasswordEncoder passwordEncoder(){	//비밀번호 암호화를 할 수 있도록 인코더 객체를 빈으로 등록.
		return PasswordEncoderFactories.createDelegatingPasswordEncoder(); //실질적 패스워드 생성
	}

}

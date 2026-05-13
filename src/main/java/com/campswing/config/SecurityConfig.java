package com.campswing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Camp Swing Outdoor는 로그인/회원/인증 기능이 없는 공개 사이트입니다.
 * Spring Security는 다음 3가지 용도로만 사용합니다:
 *   1. CSRF 토큰 (Thymeleaf 폼 위조 방지)
 *   2. 정적 리소스 permitAll
 *   3. 보안 헤더 (CSP, frameOptions)
 * formLogin, httpBasic, UserDetailsService, PasswordEncoder 등 인증 구성은 추가하지 않습니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**", "/js/**", "/img/**", "/fonts/**",
                                "/favicon.ico", "/error/**", "/error"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v1/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "img-src 'self' data:; " +
                                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                        "font-src 'self' data: https://fonts.gstatic.com; " +
                                        "script-src 'self' 'unsafe-inline'; " +
                                        "connect-src 'self'"
                        ))
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(Customizer.withDefaults());
        return http.build();
    }
}

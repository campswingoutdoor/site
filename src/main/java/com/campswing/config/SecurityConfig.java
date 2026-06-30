package com.campswing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
                // CSRF 토큰을 세션 대신 쿠키(XSRF-TOKEN)에 저장.
                // 세션 lazy 생성 → 응답 commit 충돌 회피 (party-pass 같은 큰 폼 페이지에서 발생하던 IllegalStateException 해결).
                // CsrfTokenRequestAttributeHandler(plain)로 변경하여 XOR 마스킹 비활성 — Thymeleaf 폼 hidden input과 호환.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/api/v1/**")
                )
                // CSRF 토큰을 필터 체인에서 미리 materialize → XSRF-TOKEN 쿠키가 응답 커밋 전에 기록됨.
                // 큰 폼 페이지(파티패스 등)에서 응답이 chunked 로 먼저 커밋되며 Set-Cookie 가 누락되어
                // 제출 시 403(CSRF) → whitelabel 이 발생하던 문제 해결.
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "img-src 'self' data: https: https://i.ibb.co; " +
                                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                        "font-src 'self' data: https://fonts.gstatic.com; " +
                                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                        "connect-src 'self'"
                        ))
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(Customizer.withDefaults());
        return http.build();
    }

    /**
     * CsrfFilter 직후 실행되어 지연 로딩된 CSRF 토큰을 강제로 materialize 한다.
     * getToken() 호출 시점에 CookieCsrfTokenRepository 가 XSRF-TOKEN 쿠키를 응답 헤더에 기록하므로,
     * 큰 폼 페이지에서 응답 body 가 커밋되기 전에 Set-Cookie 가 확실히 전송된다.
     */
    static final class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken(); // 토큰 값 접근 → 쿠키 기록 트리거
            }
            filterChain.doFilter(request, response);
        }
    }
}

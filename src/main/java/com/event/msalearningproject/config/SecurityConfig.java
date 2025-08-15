package com.event.msalearningproject.config;

import com.event.msalearningproject.auth.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * [기존] Web(Servlet) 기반의 보안 설정을 담당합니다.
     * 실제 API 접근 제어 규칙은 여기서 정의합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/msa/v1/members",
                                "/msa/v1/auth/login",
                                "/msa/swagger.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * [추가] WebFlux 기반의 보안 설정을 담당합니다.
     * WebClient 사용으로 WebFlux 의존성이 포함될 때, 이 설정이 없으면 403 오류가 발생할 수 있습니다.
     * 여기서는 모든 요청을 허용(permitAll)하도록 설정하여, Web(Servlet) 기반의 SecurityFilterChain이
     * 모든 인증/인가를 처리하도록 역할을 위임합니다.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChainWebflux(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/**").permitAll() // 모든 경로의 요청을 허용
                )
                .build();
    }
}

package com.gdg.oauthgooglelogin.config;

import com.gdg.oauthgooglelogin.jwt.JwtFilter;
import com.gdg.oauthgooglelogin.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration //클래스가 구성 파일임을 알려줌, Bean 객체 생성 및 관리
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig { //프로그램 설정 정의하는 클래스

    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http //rest api 설정
                .httpBasic(AbstractHttpConfigurer::disable) //기본 인증 로그인 비활성화
                .csrf(AbstractHttpConfigurer::disable) //cookie 사용 안 해서 필요 없음?
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용 안함(JWT 등 토큰 기반 인증에 적합)
                .formLogin(AbstractHttpConfigurer::disable) //기본 login form 비활성화
                .logout(AbstractHttpConfigurer::disable) //기본 logout 비활성화
                .authorizeHttpRequests(req -> req //request 인증, 인가 설정
                        .requestMatchers(
                                "/api/**",              // 콜백/토큰 교환 컨트롤러 열어둠
                                "/", "/index.html",     // 공개 랜딩
                                "/login/**", "/oauth2/**",
                                "/error", "/favicon.ico",
                                "/static/**", "/assets/**"
                        ).permitAll()
                        .requestMatchers("/gdg/**").authenticated()
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(configurationSource()))
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Access-Control-Allow-Credentials", "Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

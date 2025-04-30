package com.part3.team07.sb01deokhugamteam07.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.part3.team07.sb01deokhugamteam07.logging.MDCLoggingFilter;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.security.filter.HeaderAuthenticationFilter;
import com.part3.team07.sb01deokhugamteam07.security.filter.UserIdHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final UserIdHeaderFilter userIdHeaderFilter;
  private final HeaderAuthenticationFilter headerAuthenticationFilter;
  private final MDCLoggingFilter mdcLoggingFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable())
        .httpBasic(hb -> hb.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/users/login",
                "/",
                "/index.html",
                "/favicon.ico",
                "/error",
                "/api/users",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ).permitAll() // 위의 엔드포인트 허용
            .requestMatchers("/api/**").authenticated() // 나머지 엔드포인트 막음
        )
        .addFilterBefore(mdcLoggingFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(headerAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class) // 인증 전 헤더 필터
        .addFilterAfter(userIdHeaderFilter,
            UsernamePasswordAuthenticationFilter.class) // 인증 후 User ID 필터 추가
        .userDetailsService(userDetailsService);

    return http.build();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
  * @methodName : webSecurityCustomizer
  * @date : 2025. 4. 21. 17:33
  * @author : wongil
  * @Description: 정적 리소스 무시
  **/
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        .requestMatchers("/static/**", "/assets/**")
        .requestMatchers("/favicon.ico");
  }
}

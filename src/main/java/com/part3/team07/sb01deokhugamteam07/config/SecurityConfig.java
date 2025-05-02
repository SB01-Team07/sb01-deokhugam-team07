package com.part3.team07.sb01deokhugamteam07.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.part3.team07.sb01deokhugamteam07.logging.MDCLoggingFilter;
import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.security.PreAuthUserDetailsService;
import com.part3.team07.sb01deokhugamteam07.security.filter.UserIdHeaderFilter;
import com.part3.team07.sb01deokhugamteam07.security.filter.UserIdPreAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final MDCLoggingFilter mdcLoggingFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      DaoAuthenticationProvider daoAuthProvider,
      PreAuthenticatedAuthenticationProvider preAuthProvider) throws Exception {

    // PreAuthenticatedProcessingFilter 생성
    UserIdPreAuthenticationFilter userIdPreAuthenticationFilter = new UserIdPreAuthenticationFilter();
    userIdPreAuthenticationFilter.setAuthenticationManager(
        authenticationManager(preAuthProvider, daoAuthProvider)); // dao, PreAuthenticateAuthentication provider 주입

    http
        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/users/login",
                "/",
                "/error",
                "/api/users",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/books/popular",
                "/api/users/power",
                "/api/reviews/popular",
                "/actuator/**"
            ).permitAll() // 위의 엔드포인트 허용
            .requestMatchers("/api/**").authenticated() // 나머지 엔드포인트 막음
        )
        .addFilterBefore(mdcLoggingFilter, UsernamePasswordAuthenticationFilter.class) // MDC 로깅 필터
        .addFilterAfter(new UserIdHeaderFilter(), UsernamePasswordAuthenticationFilter.class) // 헤더 삽입 필터
        .addFilterBefore(userIdPreAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // PreAuth 필터
        .authenticationProvider(preAuthProvider) // PreAuthenticateAuthentication 프로바이더
        .authenticationProvider(daoAuthProvider) // Dao 프로바이더
        .userDetailsService(userDetailsService); // CustomUserDetailsService 설정

    return http.build();
  }

  /**
  * @methodName : preAuthenticatedAuthenticationProvider
  * @date : 2025. 4. 29. 18:23
  * @author : wongil
  * @Description: PreAuthenticated를 인증 하는 provider
  **/
  @Bean
  public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider(
      PreAuthUserDetailsService preAuthUserDetailsService) {
    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();

    provider.setPreAuthenticatedUserDetailsService(preAuthUserDetailsService);

    return provider;
  }

  /**
  * @methodName : daoAuthenticationProvider
  * @date : 2025. 4. 29. 18:24
  * @author : wongil
  * @Description: DAO를 인증 하는 provider
  **/
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
      CustomUserDetailsService userDetailsService) {

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);

    return provider;
  }

  /**
  * @methodName : authenticationManager
  * @date : 2025. 4. 29. 18:24
  * @author : wongil
  * @Description: AuthenticationManager에 preAuthProvider, daoAuthProvider 주입
   * AuthenticationManager는 Authentication을 받아서 인증, 인가하는 역할을 함
   * AuthenticationManager의 기본 구현체가 ProviderManager
  **/
  @Bean
  public AuthenticationManager authenticationManager(
      PreAuthenticatedAuthenticationProvider preAuthProvider,
      DaoAuthenticationProvider daoAuthProvider) {

    // 이렇게 들어가면 preAuthProvider와 daoAuthProvider를 순차적으로 실행해서 인증을 함
    return new ProviderManager(List.of(preAuthProvider, daoAuthProvider));
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
        .requestMatchers("/index.html")
        .requestMatchers("/favicon-32x32.png");
  }

  /**
  * @methodName : passwordEncoder
  * @date : 2025. 4. 29. 16:08
  * @author : wongil
  * @Description: 비밀번호 인코딩
  **/
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
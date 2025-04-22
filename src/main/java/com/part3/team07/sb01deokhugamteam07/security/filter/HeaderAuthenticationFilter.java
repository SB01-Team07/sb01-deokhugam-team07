package com.part3.team07.sb01deokhugamteam07.security.filter;

import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
* @package : com.part3.team07.sb01deokhugamteam07.security.filter
* @name : HeaderAuthenticationFilter.java
* @date : 2025. 4. 21. 17:29
* @author : wongil
* @Description: 요청헤더 Deokhugam-Request-User-ID에 있는 User ID로 인증 처리
**/
@Component
@RequiredArgsConstructor
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 인증 정보가 없으면
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String userIdHeader = request.getHeader("Deokhugam-Request-User-ID");

      if (userIdHeader != null) {
        UUID userId = UUID.fromString(userIdHeader);
        UserDetails userDetails = userDetailsService.loadUserById(userId);

        // 토큰 생성
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        // 토큰 집어넣기
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }

    filterChain.doFilter(request, response);
  }
}

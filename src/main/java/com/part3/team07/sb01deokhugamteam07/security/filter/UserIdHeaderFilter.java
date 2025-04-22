package com.part3.team07.sb01deokhugamteam07.security.filter;

import com.part3.team07.sb01deokhugamteam07.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
* @package : com.part3.team07.sb01deokhugamteam07.security.filter
* @name : UserIdHeaderFilter.java
* @date : 2025. 4. 21. 17:31
* @author : wongil
* @Description: 헤더(Deokhugam-Request-User-ID) 추가
**/
@Component
public class UserIdHeaderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    filterChain.doFilter(request, response);

    // 인증 정보 조회
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // 인증됐거나 익명 사용자가 아니면
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)
        && auth.getPrincipal() instanceof CustomUserDetails) {

      String userId = ((CustomUserDetails) auth.getPrincipal())
          .getId().toString();

      // 헤더 추가
      response.setHeader("Deokhugam-Request-User-ID", userId);
    }
  }
}

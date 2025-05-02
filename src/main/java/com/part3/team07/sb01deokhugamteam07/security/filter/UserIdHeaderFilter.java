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
import org.springframework.web.filter.OncePerRequestFilter;

/**
* @package : com.part3.team07.sb01deokhugamteam07.security.filter
* @name : UserIdHeaderFilter.java
* @date : 2025. 4. 29. 18:19
* @author : wongil
* @Description: 헤더에 Deokhugam-Request-User-ID 넣기
**/
public class UserIdHeaderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 다른 필터부터 실행 먼저함
    filterChain.doFilter(request, response);

    // SecurityContext에서 인증 정보 가져옴
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증 정보가 없거나 CustomUserDetails 타입의 인증된 UserDetails가 있으면
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails
        && !(authentication instanceof AnonymousAuthenticationToken)) {

      // UUID 꺼내서 헤더에 넣음
      String userId = ((CustomUserDetails) authentication.getPrincipal()).getId().toString();
      response.addHeader("Deokhugam-Request-User-ID", userId);
    }
  }
}

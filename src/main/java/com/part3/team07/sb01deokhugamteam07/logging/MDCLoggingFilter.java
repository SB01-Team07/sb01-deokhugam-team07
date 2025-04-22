package com.part3.team07.sb01deokhugamteam07.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
* @package : com.part3.team07.sb01deokhugamteam07.logging
* @name : MDCLoggingFilter.java
* @date : 2025. 4. 22. 13:20
* @author : wongil
* @Description: MDC 로깅을 위한 필터
**/
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter extends OncePerRequestFilter {

  private static final String HEADER_REQUEST_ID = "X-Request-ID";
  private static final String HEADER_CLIENT_IP = "X-Client-IP";
  private static final String HEADER_FOWARD_FOR = "X-Forwared-For";


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {

      // request UUID 설정
      String requestId = request.getHeader(HEADER_REQUEST_ID);
      if (requestId == null || requestId.isEmpty()) {
        requestId = UUID.randomUUID().toString();
      }
      MDC.put("requestId", requestId);

      // 클라이언트 ip 설정
      String forwardHeader = request.getHeader(HEADER_FOWARD_FOR);
      String clientIp = null;
      if (forwardHeader != null && !forwardHeader.isEmpty()) {
        clientIp = forwardHeader.split(".")[0].trim();
      } else {
        clientIp = request.getRemoteAddr();
      }
      MDC.put("clientIp", clientIp);

      MDC.put("requestMethod", request.getMethod());
      MDC.put("requestUrl", request.getRequestURI());

      response.setHeader(HEADER_REQUEST_ID, requestId);
      response.setHeader(HEADER_CLIENT_IP, clientIp);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}

package com.part3.team07.sb01deokhugamteam07.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
* @package : com.part3.team07.sb01deokhugamteam07.security.filter
* @name : UserIdPreAuthenticationFilter.java
* @date : 2025. 4. 29. 18:11
* @author : wongil
* @Description: AbstractPreAuthenticatedProcessingFilter를 상속받아 구현
**/
public class UserIdPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final String HEADER = "Deokhugam-Request-User-ID";

  /**
  * @methodName : getPreAuthenticatedPrincipal
  * @date : 2025. 4. 29. 18:12
  * @author : wongil
  * @Description: Deokhugam-Request-User-ID에서 사용자 UUID 꺼내기
  **/
  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getHeader(HEADER);
  }

  // credential 정보는 없음으로 사용안함
  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "";
  }
}

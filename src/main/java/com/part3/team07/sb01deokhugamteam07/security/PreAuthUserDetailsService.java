package com.part3.team07.sb01deokhugamteam07.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

/**
* @package : com.part3.team07.sb01deokhugamteam07.security
* @name : PreAuthUserDetailsService.java
* @date : 2025. 4. 29. 18:07
* @author : wongil
* @Description: PreAuthenticatedAuthenticationToken에 있는 사용자 UUID를 UserDetails의 Username(여기서는 email)과 같은 걸로 변환
**/
@Service
@RequiredArgsConstructor
public class PreAuthUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  private final CustomUserDetailsService userDetailsService;

  @Override
  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
      throws UsernameNotFoundException {

    // 토큰에서 사용자 정보(principal)을 가져오기
    String principal = (String) token.getPrincipal();

    // CustomUserDetailsService에서 유저의 UUID로 검색해서 UserDetails 반환
    return userDetailsService.loadUserById(UUID.fromString(principal));
  }
}

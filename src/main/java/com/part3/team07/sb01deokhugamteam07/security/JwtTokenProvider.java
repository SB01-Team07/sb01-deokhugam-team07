package com.part3.team07.sb01deokhugamteam07.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.security.jwt-secret}")
  private String jwtSecret;

  @Value("${jwt.security.jwt-expiration}")
  private long jwtExpirationMs;

  @Value("${jwt.security.jwt-issuer}")
  private String jwtIssuer;

  /**
  * @methodName : generateToken
  * @date : 2025. 4. 21. 11:30
  * @author : wongil
  * @Description: jwt 토큰 생성
  **/
  public String generateToken(Authentication authentication) {
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    Date now = new Date();

    return Jwts.builder()
        .issuer(jwtIssuer)
        .subject(authentication.getName())
        .issuedAt(now)
        .expiration(new Date(now.getTime() + jwtExpirationMs)) // 만료 시간
        .signWith(key) // 실제 서명
        .compact();
  }

  /**
  * @methodName : getUsername
  * @date : 2025. 4. 21. 11:40
  * @author : wongil
  * @Description: JWT에서 subject 파싱
  **/
  public String getUsername(String token) {
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

    return Jwts.parser()
        .verifyWith(key) // secret 검증
        .build()
        .parseSignedClaims(token) // 서명된 클레임 파싱
        .getPayload()
        .getSubject();
  }

  public boolean validateToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

      Jwts.parser()
          .verifyWith(key)
          .build()
          .parse(token);

      return true; // 검증 성공
    } catch (Exception e) {
      return false;
    }
  }
}

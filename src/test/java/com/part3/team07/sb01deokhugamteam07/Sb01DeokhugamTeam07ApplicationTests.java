package com.part3.team07.sb01deokhugamteam07;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.security.jwt-secret=tRsLycvAv+NdFne3uTGzRHlH4XNXGP2v/6QW5dCT0Fs=",
    "jwt.security.jwt-expiration=60000",
    "jwt.security.jwt-issuer=test"
})
class Sb01DeokhugamTeam07ApplicationTests {

  @Test
  void contextLoads() {
  }

}

package com.part3.team07.sb01deokhugamteam07;

import com.part3.team07.sb01deokhugamteam07.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class Sb01DeokhugamTeam07ApplicationTests {

  @Test
  void contextLoads() {
  }

}

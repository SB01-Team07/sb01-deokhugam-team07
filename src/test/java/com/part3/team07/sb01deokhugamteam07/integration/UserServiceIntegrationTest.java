package com.part3.team07.sb01deokhugamteam07.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtProvider jwtProvider;

  @Test
  @DisplayName("모든 응답에 Deokhugam-Request-User-ID가 있어야함")
  void checkHeader() throws Exception {
    UUID uuid = UUID.randomUUID();
    String token = jwtProvider.generateToken(uuid);

    mockMvc.perform(get("/api/books")
            .header("Authorization", "Bearer" + token))
        .andExpect(status().isOk())
        .andExpect(header().string("Deokhugam-Request-User-ID", uuid.toString()));
  }
}

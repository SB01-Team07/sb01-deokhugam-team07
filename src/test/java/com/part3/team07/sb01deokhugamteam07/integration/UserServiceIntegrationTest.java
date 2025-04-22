package com.part3.team07.sb01deokhugamteam07.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private JwtProvider jwtProvider;
//
//  @Test
//  @DisplayName("모든 응답에 Deokhugam-Request-User-ID가 있어야함")
//  void checkHeader() throws Exception {
//    UUID uuid = UUID.randomUUID();
//    String token = jwtProvider.generateToken(uuid);
//
//    mockMvc.perform(get("/api/books")
//            .header("Authorization", "Bearer" + token))
//        .andExpect(status().isOk())
//        .andExpect(header().string("Deokhugam-Request-User-ID", uuid.toString()));
//  }
}

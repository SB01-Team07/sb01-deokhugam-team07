package com.part3.team07.sb01deokhugamteam07.config;

import static org.mockito.Mockito.mock;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import java.io.IOException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

  @Bean
  @Primary
  public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
    return mock(ImageAnnotatorClient.class);
  }
}

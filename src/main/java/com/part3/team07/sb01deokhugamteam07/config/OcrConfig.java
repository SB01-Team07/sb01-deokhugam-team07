package com.part3.team07.sb01deokhugamteam07.config;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {

  @Bean
  public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
    return ImageAnnotatorClient.create();
  }
}

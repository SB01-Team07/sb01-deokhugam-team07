package com.part3.team07.sb01deokhugamteam07.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.common.collect.Lists;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class OcrConfig {

  @Bean
  public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
    String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
    if (credentialsPath == null || credentialsPath.isEmpty()) {
      throw new IOException("GOOGLE_APPLICATION_CREDENTIALS environment variable is not set or the file path is incorrect.");
    }

    GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

    return ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder().setCredentialsProvider(
        FixedCredentialsProvider.create(credentials)).build());
  }
}

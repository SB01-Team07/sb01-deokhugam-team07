package com.part3.team07.sb01deokhugamteam07.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "s3")
public class S3Config {

  @Value("${deokhugam.storage.s3.access-key}")
  private String accessKey;

  @Value("${deokhugam.storage.s3.secret-key}")
  private String secretKey;

  @Value("${deokhugam.storage.s3.region}")
  private String region;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }
}

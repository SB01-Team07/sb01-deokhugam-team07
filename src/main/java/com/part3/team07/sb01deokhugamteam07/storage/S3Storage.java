package com.part3.team07.sb01deokhugamteam07.storage;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageSaveFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "s3")
@Component
@RequiredArgsConstructor
public class S3Storage implements Storage {

  private final S3Client s3Client;

  @Value("${deokhugam.storage.s3.bucket}")
  private String bucketName;

  @Value("${deokhugam.storage.s3.region}")
  private String region;

  @Override
  public String put(FileType type, String fileName, byte[] bytes) {
    String key = resolvePath(type, fileName);

    if (exists(key)) {
      throw StorageAlreadyExistsException.withFileName(fileName);
    }

    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(bytes));


      if (type == FileType.THUMBNAIL_IMAGE) {
        return getObjectUrl(key);
      }
      return null;
    } catch (AwsServiceException e) {
      throw StorageSaveFailedException.withFileName(fileName);
    }
  }

  private boolean exists(String key) {
    try {
      s3Client.headObject(
          HeadObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .build()
      );
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    } catch (AwsServiceException e) {
      throw StorageSaveFailedException.withFileName(key);
    }
  }

  public String resolvePath(FileType type, String fileName) {
    return getSubDirByType(type) + "/" + fileName;
  }

  private String getSubDirByType(FileType type) {
    return switch (type) {
      case THUMBNAIL_IMAGE -> "thumbnail";
      case LOG -> "logs";
    };
  }

  private String getObjectUrl(String key) {
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
  }
}

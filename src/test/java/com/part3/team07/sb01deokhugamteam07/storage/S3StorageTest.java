package com.part3.team07.sb01deokhugamteam07.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageSaveFailedException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class S3StorageTest {

  private S3Client s3Client;
  private S3Storage s3Storage;

  private byte[] bytes;
  private String fileName;
  private FileType fileType;

  @BeforeEach
  void setUp() {
    s3Client = mock(S3Client.class);
    s3Storage = new S3Storage(s3Client);

    bytes = "hello".getBytes();
    fileName = "test.png";
    fileType = FileType.THUMBNAIL_IMAGE;
  }

  @Test
  @DisplayName("storage put 성공")
  void put_success() {
    // given
    willThrow(NoSuchKeyException.builder().build())
        .given(s3Client).headObject(any(HeadObjectRequest.class));

    // when
    s3Storage.put(fileType, fileName, bytes);

    // then
    then(s3Client).should().putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("storage put 실패 - 중복된 파일 이름")
  void put_fail_duplicateFileName() {
    // given
    given(s3Client.headObject(any(HeadObjectRequest.class)))
        .willReturn(HeadObjectResponse.builder().build());

    // when & then
    assertThatThrownBy(() -> s3Storage.put(fileType, fileName, bytes))
        .isInstanceOf(StorageAlreadyExistsException.class);
  }

  @Test
  @DisplayName("storage put 실패 - 저장 중 오류")
  void put_fail_awsServiceException() {
    // given
    willThrow(NoSuchKeyException.builder().build())
        .given(s3Client).headObject(any(HeadObjectRequest.class));

    willThrow(AwsServiceException.builder().build())
        .given(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

    // when & then
    assertThatThrownBy(() -> s3Storage.put(fileType, fileName, bytes))
        .isInstanceOf(StorageSaveFailedException.class);
  }

  @Test
  @DisplayName("type으로 LOG 선택 시 logs 반환")
  void getSubDirByTypeTest() {
    // given
    FileType fileType = FileType.LOG;

    // when
    String filePath = s3Storage.resolvePath(fileType, fileName);

    // then
    assertThat("logs/" + fileName).isEqualTo(filePath);
  }
}
package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageSaveFailedException;
import com.part3.team07.sb01deokhugamteam07.storage.Storage;
import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

  @Mock
  private Storage storage;

  @InjectMocks
  private StorageService storageService;

  @Nested
  @DisplayName("파일 save")
  class SaveTest {
    @Test
    @DisplayName("save 성공")
    void save_success() {
      // given
      MockMultipartFile multipartFile = new MockMultipartFile(
          "file",
          "test-image.png",
          "image/png",
          "test image content".getBytes()
      );

      // when
      storageService.save(multipartFile, FileType.THUMBNAIL_IMAGE);

      // then
      verify(storage).put(any(FileType.class), any(String.class), any(byte[].class));
    }

    @Test
    @DisplayName("save 실패 - MultipartFile.getBytes() IOException 발생")
    void save_fail_IOException() throws IOException {
      // given
      MultipartFile multipartFile = org.mockito.Mockito.mock(MultipartFile.class);
      given(multipartFile.getOriginalFilename()).willReturn("test-image.png");
      given(multipartFile.getBytes()).willThrow(new IOException("파일 읽기 실패"));

      // when & then
      assertThatThrownBy(() -> storageService.save(multipartFile, FileType.THUMBNAIL_IMAGE))
          .isInstanceOf(StorageSaveFailedException.class);
    }
  }

  @Test
  @DisplayName("generateFileName 성공")
  void generateFileName() throws Exception {
    // given
    StorageService storageService = new StorageService(null);
    MultipartFile multipartFile = new MockMultipartFile(
        "file",
        "test-image.png",
        "image/png",
        "dummy".getBytes());

    Method method = StorageService.class.getDeclaredMethod("generateFileName", MultipartFile.class);
    method.setAccessible(true);

    // when
    String generatedFileName = (String) method.invoke(storageService, multipartFile);

    // then
    assertThat(generatedFileName).endsWith(".png");
  }

}
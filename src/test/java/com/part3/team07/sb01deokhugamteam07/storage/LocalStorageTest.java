package com.part3.team07.sb01deokhugamteam07.storage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageAlreadyExistsException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LocalStorageTest {

  @Autowired
  private LocalStorage storage;

  private String fileName;
  private byte[] content;
  private FileType type;

  @BeforeEach
  void setUp() {
    fileName = "testImage.png";
    content = "Test content".getBytes();
    type = FileType.THUMBNAIL_IMAGE;
  }

  @AfterEach
  void tearDown() throws IOException {
    Path filePath = storage.resolvePath(type, fileName);
    if (Files.exists(filePath)) {
      Files.delete(filePath);
    }
  }

  @Test
  @DisplayName("thumbnailImage put 성공")
  void put_success() throws IOException {
    // given
    Path filePath = storage.resolvePath(type, fileName);

    // when
    storage.put(type, fileName, content);

    // then
    assertThat(Files.exists(filePath)).isTrue();
    byte[] fileContent = Files.readAllBytes(filePath);
    assertThat(fileContent).isEqualTo(content);
  }

  @Test
  @DisplayName("thumbnailImage put 실패 - 있는 파일")
  void put_fail_duplicateFileName() throws IOException {
    // given
    Path filePath = storage.resolvePath(type, fileName);
    Files.createDirectories(filePath.getParent());
    Files.write(filePath, content);

    // when & then
    assertThrows(StorageAlreadyExistsException.class, () -> {
      storage.put(type, fileName, content);
    });
  }
}
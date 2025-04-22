package com.part3.team07.sb01deokhugamteam07.storage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
class LocalThumbnailImageStorageTest {

  @Autowired
  private LocalThumbnailImageStorage storage;

  private String fileName;
  private byte[] content;

  @BeforeEach
  void setUp() {
    fileName = "testImage.png";
    content = "Test content".getBytes();
  }

  @AfterEach
  void tearDown() throws IOException {
    Path filePath = storage.resolvePath(fileName);
    if (Files.exists(filePath)) {
      Files.delete(filePath);
    }
  }

  @Test
  @DisplayName("thumbnailImage put 성공")
  void put() throws IOException {
    // when
    storage.put(fileName, content);

    // then
    Path filePath = storage.resolvePath(fileName);
    assertThat(Files.exists(filePath)).isTrue();

    byte[] fileContent = Files.readAllBytes(filePath);
    assertThat(fileContent).isEqualTo(content);
  }

  @Test
  @DisplayName("thumbnailImage get 성공")
  void get() {
    // given
    storage.put(fileName, content);

    // when
    String fileUrl = storage.get(fileName);

    // then
    assertThat(fileUrl).isEqualTo("/storage/" + fileName);
  }

}
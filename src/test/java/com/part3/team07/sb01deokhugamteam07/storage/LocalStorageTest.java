package com.part3.team07.sb01deokhugamteam07.storage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageInitException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
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
  @DisplayName("init 실패 - 디렉토리 생성 중 IOException 발생")
  void init_fail_IOException() {
    // given
    Path rootPath = Path.of("fake/path");
    LocalStorage localStorage = new LocalStorage(rootPath);

    try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
      filesMock.when(() -> Files.exists(rootPath)).thenReturn(false);
      filesMock.when(() -> Files.createDirectories(rootPath)).thenThrow(new IOException("디렉토리 생성 실패"));

      // when & then
      assertThatThrownBy(localStorage::init)
          .isInstanceOf(StorageInitException.class);
    }
  }

  @Nested
  @DisplayName("파일 put")
  class PutTest {
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

  @Test
  @DisplayName("getSubDirByType 성공")
  void getSubDirByType() throws Exception {
    // given
    LocalStorage localStorage = new LocalStorage(Path.of("fake/root"));

    // private 메서드라 리플렉션으로 호출해야 함
    Method method = LocalStorage.class.getDeclaredMethod("getSubDirByType", FileType.class);
    method.setAccessible(true);

    // when
    String thumbnailResult = (String) method.invoke(localStorage, FileType.THUMBNAIL_IMAGE);
    String logResult = (String) method.invoke(localStorage, FileType.LOG);

    // then
    assertThat(thumbnailResult).isEqualTo("thumbnail");
    assertThat(logResult).isEqualTo("logs");
  }

}
package com.part3.team07.sb01deokhugamteam07.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "local")
@Component
public class LocalThumbnailImageStorage implements ThumbnailImageStorage {

  private final Path root;

  public LocalThumbnailImageStorage(
      @Value("${deokhugam.storage.local.root-path}") Path root
  ) {
    this.root = root;
  }

  @Override
  public void put(String fileName, byte[] bytes) {
    try (OutputStream outputStream = Files.newOutputStream(root.resolve(fileName))) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String get(String fileName) {
    return "/storage/" + fileName;
  }

  public Path resolvePath(String fileName) {
    return root.resolve(fileName);
  }
}

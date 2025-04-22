package com.part3.team07.sb01deokhugamteam07.storage;

import com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage.ThumbnailImageAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage.ThumbnailImageNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage.ThumbnailImageStorageException;
import com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage.ThumbnailImageStorageInitException;
import jakarta.annotation.PostConstruct;
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

  @PostConstruct
  public void init() {
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new ThumbnailImageStorageInitException();
      }
    }
  }

  @Override
  public void put(String fileName, byte[] bytes) {
    Path filePath = resolvePath(fileName);
    if (Files.exists(filePath)) {
      throw new ThumbnailImageAlreadyExistsException();
    }

    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new ThumbnailImageStorageException();
    }
  }

  @Override
  public String get(String fileName) {
    Path filePath = resolvePath(fileName);
    if (Files.notExists(filePath)) {
      throw new ThumbnailImageNotFoundException();
    }

    return "/storage/" + fileName;
  }

  public Path resolvePath(String fileName) {
    return root.resolve(fileName);
  }
}

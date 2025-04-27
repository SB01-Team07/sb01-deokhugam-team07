package com.part3.team07.sb01deokhugamteam07.storage;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.file.StorageAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.file.StorageInitException;
import com.part3.team07.sb01deokhugamteam07.exception.file.StorageSaveFailedException;
import com.part3.team07.sb01deokhugamteam07.exception.thumbnailImage.ThumbnailImageStorageException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "local")
@Component
public class LocalStorage implements Storage {

  private final Path root;

  public LocalStorage(
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
        throw StorageInitException.withPath(root.toString());
      }
    }
  }

  @Override
  public void put(FileType type, String fileName, byte[] bytes) {
    Path filePath = resolvePath(type, fileName);

    try {
      Files.createDirectories(filePath.getParent());
      try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
        outputStream.write(bytes);
      }
    } catch (FileAlreadyExistsException e) {
      throw StorageAlreadyExistsException.withFileName(fileName);
    } catch (IOException e) {
      throw StorageSaveFailedException.withFileName(fileName);
    }
  }

  public Path resolvePath(FileType type, String fileName) {
    return root.resolve(getSubDirByType(type)).resolve(fileName);
  }

  private String getSubDirByType(FileType type) {
    return switch (type) {
      case THUMBNAIL_IMAGE -> "thumbnail";
      case LOG -> "log";
    };
  }
}

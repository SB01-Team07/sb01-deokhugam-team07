package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;
import com.part3.team07.sb01deokhugamteam07.exception.storage.StorageSaveFailedException;
import com.part3.team07.sb01deokhugamteam07.storage.Storage;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

  private final Storage storage;

  public String save(MultipartFile thumbnailImage, FileType fileType) {
    String fileName = generateFileName(thumbnailImage);
    try {
      storage.put(fileType, fileName, thumbnailImage.getBytes());
    } catch (IOException e) {
      throw StorageSaveFailedException.withFileName(fileName);
    }

    return fileName;
  }

  private String generateFileName(MultipartFile file) {
    String originalFileName = file.getOriginalFilename();
    String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";

    return UUID.randomUUID().toString() + extension;
  }
}

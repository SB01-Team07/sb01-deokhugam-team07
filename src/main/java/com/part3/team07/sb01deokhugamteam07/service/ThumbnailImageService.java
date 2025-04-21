package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.storage.ThumbnailImageStorage;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailImageService {

  private final ThumbnailImageStorage thumbnailImageStorage;

  public String save(MultipartFile thumbnailImage) {
    String fileName = generateFileName(thumbnailImage);
    try {
      thumbnailImageStorage.put(fileName, thumbnailImage.getBytes());
    } catch (IOException e) {
      throw new RuntimeException();
    }

    return fileName;
  }

  private String generateFileName(MultipartFile file) {
    String originalFileName = file.getOriginalFilename();
    String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";

    return UUID.randomUUID().toString() + extension;
  }
}

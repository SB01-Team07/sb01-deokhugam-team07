package com.part3.team07.sb01deokhugamteam07.storage;

import com.part3.team07.sb01deokhugamteam07.entity.FileType;

public interface Storage {
  void put(FileType type, String fileName, byte[] bytes);
}

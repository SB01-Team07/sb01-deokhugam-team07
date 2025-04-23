package com.part3.team07.sb01deokhugamteam07.storage;

public interface ThumbnailImageStorage {
  void put(String fileName, byte[] bytes);
  String get(String fileName);
}

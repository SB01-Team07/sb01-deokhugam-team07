package com.part3.team07.sb01deokhugamteam07.mapper;

import com.part3.team07.sb01deokhugamteam07.client.dto.NaverBookRssResponse;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class NaverBookMapper {

  public NaverBookDto toDto(NaverBookRssResponse.Item item) {
    return new NaverBookDto(
        item.getTitle(),
        item.getAuthor(),
        item.getDescription(),
        item.getPublisher(),
        parsePubDate(item.getPubdate()),
        extractIsbn13(item.getIsbn()),
        downloadThumbnail(item.getImage())
    );
  }

  private LocalDate parsePubDate(String pubdate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    return LocalDate.parse(pubdate, formatter);
  }

  private String extractIsbn13(String isbn) {
    return Arrays.stream(isbn.split(" "))
        .filter(s -> s.length() == 13)
        .findFirst()
        .orElse(isbn);
  }

  private byte[] downloadThumbnail(String url) {
    try (InputStream in = new URL(url).openStream()) {
      return in.readAllBytes();
    } catch (IOException e) {
      return null;
    }
  }
}

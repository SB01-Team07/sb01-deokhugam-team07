package com.part3.team07.sb01deokhugamteam07.client;

import com.part3.team07.sb01deokhugamteam07.client.dto.NaverBookRssResponse;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.mapper.NaverBookMapper;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class NaverBookClient {

  private final RestTemplate restTemplate;
  private final NaverBookMapper naverBookMapper;

  @Value("${naver.book.client-id}")
  private String clientId;

  @Value("${naver.book.client-secret}")
  private String clientSecret;

  public NaverBookDto searchByIsbn(String isbn) {
    URI uri = UriComponentsBuilder
        .fromUriString("https://openapi.naver.com")
        .path("/v1/search/book_adv.xml")
        .queryParam("d_isbn", isbn)
        .encode()
        .build()
        .toUri();

    RequestEntity<Void> req = RequestEntity
        .get(uri)
        .header("X-Naver-Client-Id", clientId)
        .header("X-Naver-Client-Secret", clientSecret)
        .build();

    ResponseEntity<NaverBookRssResponse> resp = restTemplate.exchange(req, NaverBookRssResponse.class);
    NaverBookRssResponse.Item item = Optional.ofNullable(resp.getBody())
        .map(NaverBookRssResponse::getItems)
        .flatMap(items -> items.stream().findFirst())
        .orElseThrow(() -> BookNotFoundException.withIsbn(isbn));

    return naverBookMapper.toDto(item);
  }
}

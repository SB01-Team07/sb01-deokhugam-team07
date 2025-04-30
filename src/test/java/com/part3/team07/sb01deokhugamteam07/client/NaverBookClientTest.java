package com.part3.team07.sb01deokhugamteam07.client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.part3.team07.sb01deokhugamteam07.client.dto.NaverBookRssResponse;
import com.part3.team07.sb01deokhugamteam07.client.dto.NaverBookRssResponse.Item;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.mapper.NaverBookMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class NaverBookClientTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private NaverBookMapper naverBookMapper;

  @InjectMocks
  private NaverBookClient naverBookClient;

  @Test
  @DisplayName("searchByIsbn 성공")
  void searchByIsbn_success() {
    // given
    String isbn = "9788960515529";

    Item mockItem = new Item();
    ReflectionTestUtils.setField(mockItem, "title", "자바의 정석");
    ReflectionTestUtils.setField(mockItem, "author", "남궁성");
    ReflectionTestUtils.setField(mockItem, "description", "자바를 완벽히 이해할 수 있는 책");
    ReflectionTestUtils.setField(mockItem, "publisher", "도우출판");
    ReflectionTestUtils.setField(mockItem, "pubdate", "2013-01-01");
    ReflectionTestUtils.setField(mockItem, "isbn", "8960515523 9788960515529");
    ReflectionTestUtils.setField(mockItem, "image", "http://example.com/image.jpg");

    NaverBookRssResponse.Channel channel = new NaverBookRssResponse.Channel();
    ReflectionTestUtils.setField(channel, "items", List.of(mockItem));

    NaverBookRssResponse mockResponse = new NaverBookRssResponse();
    ReflectionTestUtils.setField(mockResponse, "channel", channel);

    NaverBookDto expectedDto = new NaverBookDto(
        "자바의 정석", "남궁성", "자바를 완벽히 이해할 수 있는 책",
        "도우출판", null, "9788960515529", null
    );

    given(restTemplate.exchange(
        any(), eq(NaverBookRssResponse.class))
    ).willReturn(ResponseEntity.ok(mockResponse));

    given(naverBookMapper.toDto(mockItem))
        .willReturn(expectedDto);

    // when
    NaverBookDto actual = naverBookClient.searchByIsbn(isbn);

    // then
    Assertions.assertThat(actual).isEqualTo(expectedDto);
  }

  @Test
  @DisplayName("searchByIsbn 실패 - 검색 결과 없음")
  void searchByIsbn_fail_BookNotFound() {
    // given
    String isbn = "1111111111111";

    given(restTemplate.exchange(
        any(), eq(NaverBookRssResponse.class))
    ).willReturn(ResponseEntity.ok(null));

    // when & then
    assertThrows(BookNotFoundException.class,
        () -> naverBookClient.searchByIsbn(isbn)
    );
  }
}
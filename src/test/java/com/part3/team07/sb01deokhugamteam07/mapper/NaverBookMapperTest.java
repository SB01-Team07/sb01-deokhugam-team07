package com.part3.team07.sb01deokhugamteam07.mapper;

import com.part3.team07.sb01deokhugamteam07.client.dto.NaverBookRssResponse.Item;
import com.part3.team07.sb01deokhugamteam07.dto.book.NaverBookDto;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class NaverBookMapperTest {

  private final NaverBookMapper mapper = new NaverBookMapper();

  @Test
  @DisplayName("toDto 성공")
  void toDto_success() {
    // given
    Item item = new Item();
    ReflectionTestUtils.setField(item, "title", "자바의 정석");
    ReflectionTestUtils.setField(item, "author", "남궁성");
    ReflectionTestUtils.setField(item, "description", "자바를 완벽히 이해할 수 있는 책");
    ReflectionTestUtils.setField(item, "publisher", "도우출판");
    ReflectionTestUtils.setField(item, "pubdate", "20130101");
    ReflectionTestUtils.setField(item, "isbn", "8960515523 9788960515529");
    ReflectionTestUtils.setField(item, "image", "http://example.com/image.jpg");

    // when
    NaverBookDto dto = mapper.toDto(item);

    // then
    assertThat(dto.title()).isEqualTo("자바의 정석");
    assertThat(dto.author()).isEqualTo("남궁성");
    assertThat(dto.description()).isEqualTo("자바를 완벽히 이해할 수 있는 책");
    assertThat(dto.publisher()).isEqualTo("도우출판");
    assertThat(dto.publishedDate()).isEqualTo(LocalDate.of(2013, 1, 1));
    assertThat(dto.isbn()).isEqualTo("9788960515529");
    assertThat(dto.thumbnailImage()).isNull();
  }
}

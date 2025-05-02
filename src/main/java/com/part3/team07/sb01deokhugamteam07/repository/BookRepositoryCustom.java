package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import java.time.LocalDateTime;
import java.util.List;

public interface BookRepositoryCustom {
  List<Book> findBooksWithCursor(String keyword, String sort, String order,
      String cursor, LocalDateTime after, int size);
  long countByKeyword(String keyword);

}

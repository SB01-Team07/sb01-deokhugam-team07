package com.part3.team07.sb01deokhugamteam07.repository;


import com.part3.team07.sb01deokhugamteam07.entity.Book;
import java.util.List;
import java.util.UUID;

import com.part3.team07.sb01deokhugamteam07.repository.querydsl.BookRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {

  boolean existsByIsbn(String isbn);

  List<Book> findByIsDeletedFalseOrderByCreatedAtAsc();

}

package com.part3.team07.sb01deokhugamteam07.repository;


import com.part3.team07.sb01deokhugamteam07.entity.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {

  boolean existsByIsbn(String isbn);

  Optional<Book> findByIdAndIsDeletedFalse(UUID id);
  List<Book> findAllByIsDeletedFalse();

  List<Book> findByIsDeletedFalseOrderByCreatedAtAsc();

}

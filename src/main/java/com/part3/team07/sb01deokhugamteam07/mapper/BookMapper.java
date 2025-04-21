package com.part3.team07.sb01deokhugamteam07.mapper;

import com.part3.team07.sb01deokhugamteam07.dto.book.BookDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

  BookDto toDto(Book book);
}

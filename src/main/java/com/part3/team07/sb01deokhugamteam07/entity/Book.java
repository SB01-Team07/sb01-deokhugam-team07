package com.part3.team07.sb01deokhugamteam07.entity;

import com.part3.team07.sb01deokhugamteam07.entity.base.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseSoftDeletableEntity {

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String publisher;

  @Column(nullable = false)
  private LocalDate publishDate;

  @Column(unique = true, updatable = false)
  private String isbn;

  private String thumbnailUrl;

  @Column(nullable = false)
  private int reviewCount;

  @Column(nullable = false)
  private double rating;

  public Book(String title, String author, String description, String publisher,
      LocalDate publishDate, String isbn, String thumbnailUrl) {
    this.title = title;
    this.author = author;
    this.description = description;
    this.publisher = publisher;
    this.publishDate = publishDate;
    this.isbn = isbn;
    this.thumbnailUrl = thumbnailUrl;
    this.reviewCount = 0;
    this.rating = 0;
  }
}

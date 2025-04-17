package com.part3.team07.sb01deokhugamteam07.entity;

import com.part3.team07.sb01deokhugamteam07.entity.base.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "reviews")
public class Review extends BaseSoftDeletableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @Column(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @Column(name = "book_id", nullable = false)
  private Book book;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private int rating;

  @Column(nullable = false)
  private int likeCount;

  @Column(nullable = false)
  private int commentCount;
}

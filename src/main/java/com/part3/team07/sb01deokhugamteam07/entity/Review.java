package com.part3.team07.sb01deokhugamteam07.entity;

import com.part3.team07.sb01deokhugamteam07.entity.base.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseSoftDeletableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private int rating;

  @Column(nullable = false)
  private int likeCount;

  @Column(nullable = false)
  private int commentCount;

  public boolean isReviewer(UUID userId) {
    return (this.user.getId().equals(userId));
  }

  public void update(String content, int rating) {
    this.content = content;
    this.rating = rating;
  }

  public void softDelete(){
    delete();
  }

}

package com.part3.team07.sb01deokhugamteam07.repository.querydsl;

import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommentRepositoryCustom {

  List<Comment> findCommentByCursor(
      Review review,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit,
      String sortBy
  );

  long countByReview(Review review);

  Map<UUID, Long> countCommentsByReviewIds(List<UUID> reviewIds);
}

package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepositoryCustom {

  List<Comment> findCommentByCursor(
      Review review,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  );
}

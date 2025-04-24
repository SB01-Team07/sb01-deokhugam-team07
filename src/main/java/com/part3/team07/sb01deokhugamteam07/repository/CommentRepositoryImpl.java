package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.QComment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findCommentByCursor(
      Review review,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {
    QComment comment = QComment.comment;

    // 일치하는 리뷰의 댓글 중에 논리삭제 되지 않은 댓글을 가져온다.
    var query = queryFactory
        .selectFrom(comment)
        .where(
            comment.review.eq(review),
            comment.isDeleted.isFalse()
        );

    if ("ASC".equalsIgnoreCase(direction)) {
      if (cursor != null) {
        LocalDateTime parsedCursor = LocalDateTime.parse(cursor);
        query = query.where(comment.createdAt.gt(parsedCursor));
      } else if (after != null) {
        query = query.where(comment.createdAt.gt(after));
      }
    } else {
      if (cursor != null) {
        LocalDateTime parsedCursor = LocalDateTime.parse(cursor);
        query = query.where(comment.createdAt.lt(parsedCursor));
      } else if (after != null) {
        query = query.where(comment.createdAt.lt(after));
      }
    }

    OrderSpecifier<LocalDateTime> order = "ASC".equalsIgnoreCase(direction)
        ? comment.createdAt.asc()
        : comment.createdAt.desc();

    return query
        .orderBy(order)
        .limit(limit + 1)
        .fetch();
  }
}

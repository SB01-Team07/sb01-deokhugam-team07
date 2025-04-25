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

    // 요구사항 기본 조건 , 추후에 정렬 조건 추가 되면 바로 적용
    String sortBy = "createdAt";
    
    OrderSpecifier<?> order;
    switch (sortBy) {
      case "createdAt":
        order = "ASC".equalsIgnoreCase(direction)
            ? comment.createdAt.asc()
            : comment.createdAt.desc();

        if (cursor != null) {
          LocalDateTime parsedCursor = LocalDateTime.parse(cursor);
          query = "ASC".equalsIgnoreCase(direction)
              ? query.where(comment.createdAt.gt(parsedCursor))
              : query.where(comment.createdAt.lt(parsedCursor));
        }

        if (after != null) {
          query = query.where(comment.createdAt.gt(after));
        }
        break;

      default:
        throw new IllegalArgumentException(sortBy);
    }

    return query
        .orderBy(order)
        .limit(limit + 1)
        .fetch();
  }
}

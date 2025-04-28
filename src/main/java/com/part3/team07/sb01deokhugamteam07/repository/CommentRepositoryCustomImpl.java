package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.QComment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findCommentByCursor(
      Review review,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit,
      String sortBy
  ) {
    QComment comment = QComment.comment;

    //일치하는 리뷰의 댓글 중에 논리삭제 되지 않은 댓글을 가져온다.
    var query = queryFactory
        .selectFrom(comment)
        .where(
            comment.review.eq(review),
            comment.isDeleted.isFalse()
        );

    //커서 조건
    BooleanExpression cursorCondition = getCursorCondition(sortBy, direction, cursor, after);
    if (cursorCondition != null) {
      query = query.where(cursorCondition);
    }

    //정렬 조건
    OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(sortBy, direction);
    query = query
        .orderBy(orderSpecifiers)
        .limit(limit + 1);

    return query.fetch();
  }

  //커서 조건
  private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy, String direction) {
    QComment comment = QComment.comment;
    boolean isDesc = "DESC".equalsIgnoreCase(direction);

    switch (sortBy) {
      case "createdAt":
        return new OrderSpecifier[]{
            isDesc ? comment.createdAt.desc() : comment.createdAt.asc()
        };

      default:
        throw new IllegalArgumentException(sortBy);
    }
  }

  //정렬 조건
  private BooleanExpression getCursorCondition(
      String sortBy,
      String direction,
      String cursor,
      LocalDateTime after
  ) {
    QComment comment = QComment.comment;
    boolean isDesc = "DESC".equalsIgnoreCase(direction);

    if ((cursor == null || cursor.isBlank())&& after == null) {
      return null;
    }

    switch (sortBy) {
      case "createdAt":
        if (cursor != null && !cursor.isBlank()) {
          LocalDateTime parsed = LocalDateTime.parse(cursor);
          return isDesc ? comment.createdAt.lt(parsed) : comment.createdAt.gt(parsed);
        }
        break;
    }

    return null;
  }

}

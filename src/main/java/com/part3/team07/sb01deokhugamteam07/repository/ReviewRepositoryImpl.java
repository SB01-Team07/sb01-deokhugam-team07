package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.QBook;
import com.part3.team07.sb01deokhugamteam07.entity.QLike;
import com.part3.team07.sb01deokhugamteam07.entity.QReview;
import com.part3.team07.sb01deokhugamteam07.entity.QUser;
import com.part3.team07.sb01deokhugamteam07.type.ReviewDirection;
import com.part3.team07.sb01deokhugamteam07.type.ReviewOrderBy;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;
    private final QUser user = QUser.user;
    private final QBook book = QBook.book;
    private final QLike like = QLike.like;

    @Override
    public List<Tuple> findAll(UUID userId, UUID bookId, String keyword, ReviewOrderBy orderBy, ReviewDirection direction,
                               String cursor, LocalDateTime after, int limit, UUID requestUserId) {

        return queryFactory.select(review, like)
                .from(review)
                .join(review.user, user).fetchJoin()
                .join(review.book, book).fetchJoin()
                .leftJoin(like).on(
                        like.reviewId.eq(review.id)
                                .and(like.userId.eq(requestUserId))
                                .and(like.isDeleted.isFalse())
                )
                .where(ExpressionUtils.allOf(
                        review.isDeleted.isFalse(),
                        userIdEq(userId),
                        bookIdEq(bookId),
                        keywordContains(keyword),
                        cursorLt(cursor),
                        afterLt(after)
                ))
                .orderBy(primarySort(orderBy, direction), review.createdAt.desc())
                .limit(limit + 1)
                .fetch();
    }

    private BooleanExpression userIdEq(UUID userId) {
        return userId != null ? review.user.id.eq(userId) : null;
    }

    private BooleanExpression bookIdEq(UUID bookId) {
        return bookId != null ? review.book.id.eq(bookId) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return user.nickname.containsIgnoreCase(keyword)
                .or(book.title.containsIgnoreCase(keyword))
                .or(review.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression cursorLt(String cursor) {
        return cursor != null ? review.id.lt(UUID.fromString(cursor)) : null;
    }

    private BooleanExpression afterLt(LocalDateTime after) {
        return after != null ? review.createdAt.lt(after) : null;
    }

    private OrderSpecifier<?> primarySort(ReviewOrderBy orderBy, ReviewDirection direction) {
        if (orderBy == ReviewOrderBy.RATING) {
            return direction == ReviewDirection.DESC ? review.rating.desc() : review.rating.asc();
        } else {
            return direction == ReviewDirection.DESC ? review.createdAt.desc() : review.createdAt.asc();
        }
    }
}

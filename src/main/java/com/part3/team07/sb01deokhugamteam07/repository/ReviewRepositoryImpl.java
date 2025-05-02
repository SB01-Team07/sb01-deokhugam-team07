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

import static com.part3.team07.sb01deokhugamteam07.entity.QBook.*;
import static com.part3.team07.sb01deokhugamteam07.entity.QLike.*;
import static com.part3.team07.sb01deokhugamteam07.entity.QReview.*;
import static com.part3.team07.sb01deokhugamteam07.entity.QUser.*;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

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
                        cursorCondition(cursor, after, orderBy)
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

    private BooleanExpression cursorCondition(String cursor, LocalDateTime after, ReviewOrderBy orderBy) {
        if (orderBy == ReviewOrderBy.RATING && cursor != null && after != null) {
            Integer rating = Integer.parseInt(cursor);
            return review.rating.lt(rating)
                    .or(review.rating.eq(rating).and(review.createdAt.lt(after)));
        } else if (orderBy == ReviewOrderBy.CREATED_AT && after != null) {
            return review.createdAt.lt(after);
        }
        return null;
    }

    private OrderSpecifier<?> primarySort(ReviewOrderBy orderBy, ReviewDirection direction) {
        return switch (orderBy) {
            case RATING -> direction == ReviewDirection.DESC ? review.rating.desc() : review.rating.asc();
            case CREATED_AT -> direction == ReviewDirection.DESC ? review.createdAt.desc() : review.createdAt.asc();
        };
    }
}

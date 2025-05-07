package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.type.ReviewDirection;
import com.part3.team07.sb01deokhugamteam07.type.ReviewOrderBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

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
    public List<ReviewDto> findAll(UUID userId, UUID bookId, String keyword, ReviewOrderBy orderBy, ReviewDirection direction,
                                   String cursor, LocalDateTime after, int limit, UUID requestUserId) {

        return queryFactory
                .select(Projections.constructor(ReviewDto.class,
                        review.id,
                        book.id,
                        book.title,
                        book.thumbnailUrl,
                        user.id,
                        user.nickname,
                        review.content,
                        review.rating,
                        review.likeCount,
                        review.commentCount,
                        like.id.isNotNull(),
                        review.createdAt,
                        review.updatedAt
                ))
                .from(review)
                .join(review.user, user)
                .join(review.book, book)
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
                        cursorCondition(cursor, after, orderBy, direction)
                ))
                .orderBy(
                        buildSortSpecifiers(orderBy, direction))
                .limit(limit)
                .fetch();
    }

    private BooleanExpression userIdEq(UUID userId) {
        return userId != null ? review.user.id.eq(userId) : null;
    }

    private BooleanExpression bookIdEq(UUID bookId) {
        return bookId != null ? review.book.id.eq(bookId) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        return user.nickname.containsIgnoreCase(keyword)
                .or(book.title.containsIgnoreCase(keyword))
                .or(review.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression cursorCondition(String cursor, LocalDateTime after, ReviewOrderBy orderBy, ReviewDirection direction) {
        return switch (orderBy) {
            case RATING -> ratingCursorCondition(cursor, after, direction);
            case CREATED_AT -> createdAtCursorCondition(after, direction);
        };
    }

    private BooleanExpression ratingCursorCondition(String cursor, LocalDateTime after, ReviewDirection direction) {
        if (cursor == null || after == null) return null;

        int rating = Integer.parseInt(cursor);
        return switch (direction) {
            case DESC -> review.rating.lt(rating)
                    .or(review.rating.eq(rating).and(review.createdAt.lt(after)));
            case ASC -> review.rating.gt(rating)
                    .or(review.rating.eq(rating).and(review.createdAt.gt(after)));
        };
    }

    private BooleanExpression createdAtCursorCondition(LocalDateTime after, ReviewDirection direction) {
        if (after == null) return null;

        return switch (direction) {
            case DESC -> review.createdAt.lt(after);
            case ASC -> review.createdAt.gt(after);
        };
    }

    private OrderSpecifier<?>[] buildSortSpecifiers(ReviewOrderBy orderBy, ReviewDirection direction) {
        return switch (orderBy) {
            case RATING -> direction == ReviewDirection.DESC
                    ? new OrderSpecifier[]{review.rating.desc(), review.createdAt.desc()}
                    : new OrderSpecifier[]{review.rating.asc(), review.createdAt.asc()};
            case CREATED_AT -> direction == ReviewDirection.DESC
                    ? new OrderSpecifier[]{review.createdAt.desc()}
                    : new OrderSpecifier[]{review.createdAt.asc()};
        };
    }

    @Override
    public long count(UUID userId, UUID bookId, String keyword) {
        return queryFactory
                .select(review.count())
                .from(review)
                .join(review.user, user)
                .join(review.book, book)
                .where(ExpressionUtils.allOf(
                        review.isDeleted.isFalse(),
                        userIdEq(userId),
                        bookIdEq(bookId),
                        keywordContains(keyword)
                ))
                .fetchOne();
    }

    @Override
    public List<Review> findChangedSince(LocalDateTime cutoff) {
        return queryFactory
                .selectFrom(review)
                .where(
                        review.isDeleted.isFalse(),
                        review.createdAt.goe(cutoff).or(review.updatedAt.goe(cutoff))
                )
                .fetch();
    }
}
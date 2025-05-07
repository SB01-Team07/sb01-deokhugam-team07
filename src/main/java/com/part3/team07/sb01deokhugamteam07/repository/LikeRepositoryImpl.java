package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.QLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.part3.team07.sb01deokhugamteam07.entity.QLike.*;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<UUID, Long> countLikesByReviewIds(List<UUID> reviewIds) {

        return queryFactory
                .select(like.reviewId, like.count())
                .from(like)
                .where(
                        like.isDeleted.isFalse(),
                        like.reviewId.in(reviewIds)
                )
                .groupBy(like.reviewId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(like.reviewId),
                        tuple -> tuple.get(like.count())
                ));
    }
}

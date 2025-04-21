package com.part3.team07.sb01deokhugamteam07.mapper;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;

public class ReviewMapper {

    public static ReviewDto toDto(User user, Book book, Review review) {
        return new ReviewDto(
                review.getId().toString(),
                book.getId().toString(),
                book.getTitle(),
                book.getThumbnailUrl(),
                user.getId().toString(),
                user.getNickname(),
                review.getContent(),
                review.getRating(),
                review.getLikeCount(),
                review.getCommentCount(),
                false, // likeByMe는 기본 false
                review.getCreatedAt().toString(),
                review.getUpdatedAt().toString()
        );
    }
}

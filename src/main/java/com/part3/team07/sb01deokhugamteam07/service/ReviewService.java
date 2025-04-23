package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.mapper.ReviewMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto create(ReviewCreateRequest request) {
        log.debug("리뷰 생성 시작: {}", request);
        if(reviewRepository.existsByUserIdAndBookId(request.userId(), request.bookId())) {
            throw new IllegalArgumentException("이미 해당 도서에 대한 리뷰가 존재합니다.");
        }
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용가 존재하지 않습니다."));
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("책이 존재하지 않습니다."));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .content(request.content())
                .rating(request.rating())
                .likeCount(0)
                .commentCount(0)
                .build();
        reviewRepository.save(review);

        log.info("리뷰 생성 완료: id={}, userId={}, bookId{}", review.getId(), user.getId(), book.getId());
        return ReviewMapper.toDto(review);
    }

    @Transactional(readOnly = true)
    public ReviewDto find(UUID reviewId) {
        log.debug("리뷰 상세 조회 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        log.info("리뷰 상세 조회 완료: id={}", reviewId);
        return ReviewMapper.toDto(review);
    }

    @Transactional
    public ReviewDto update(UUID userId, UUID reviewId, ReviewUpdateRequest request) {
        return null;
    }
}

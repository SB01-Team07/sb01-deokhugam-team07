package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewLikeDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Like;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.mapper.ReviewMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.LikeRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

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
        log.debug("리뷰 수정 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if(!review.isReviewer(userId)){
            throw new IllegalArgumentException("본인이 작성한 리뷰가 아닙니다."); //403 - 리뷰 수정 권한 없음
        }
        review.update(request.content(), request.rating());
        log.info("리뷰 수정 완료: id={}", reviewId);
        return ReviewMapper.toDto(review);
    }

    @Transactional
    public void softDelete(UUID userId, UUID reviewId){
        log.debug("리뷰 논리 삭제 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if(!review.isReviewer(userId)){
            throw new IllegalArgumentException("본인이 작성한 리뷰가 아닙니다."); //403 - 리뷰 삭제 권한 없음
        }

        // TODO 댓글 논리 삭제 로직 추가
        // TODO 리뷰 논리 삭제 로직 추가

        review.softDelete();
        log.info("리뷰 논리 삭제 완료: id={}", reviewId);
    }

    @Transactional
    public void softDeleteAllByBook(Book book) {
        List<Review> reviews = reviewRepository.findAllByBook(book);
        log.info("{}개의 리뷰를 논리 삭제 시작. bookId={}", reviews.size(), book.getId());
        reviews.forEach(review -> {
            review.softDelete();
            // TODO 댓글 논리 삭제 로직 추가
            // TODO 리뷰 논리 삭제 로직 추가
        });
        log.info("book 이 가진 모든 리뷰 논리 삭제 완료. bookId={}", book.getId());
    }

    @Transactional
    public void softDeleteAllByUser(User user) {
        List<Review> reviews = reviewRepository.findAllByUser(user);
        log.info("{}개의 리뷰를 논리 삭제 시작. userId={}", reviews.size(), user.getId());
        reviews.forEach(review -> {
            review.softDelete();
            // TODO 댓글 논리 삭제 로직 추가
            // TODO 리뷰 논리 삭제 로직 추가
        });
        log.info("사용자가 작성한 모든 리뷰 논리 삭제 완료. userId={}", user.getId());
    }

    @Transactional
    public void hardDelete(UUID userId, UUID reviewId){
        log.debug("리뷰 물리 삭제 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));//404 리뷰 정보 없음
        if(!review.isReviewer(userId)){
            throw new IllegalArgumentException("본인이 작성한 리뷰가 아닙니다."); //403 - 리뷰 삭제 권한 없음
        }
        reviewRepository.delete(review);
        log.info("리뷰 물리 삭제 완료: id={}", reviewId);
    }

    @Transactional
    public ReviewLikeDto toggleLike(UUID reviewId, UUID userId){
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다.")); //404 리뷰 정보 없음

        likeRepository.findByReviewIdAndUserId(reviewId, userId);

        return null;
    }

    private ReviewLikeDto addLike(UUID userId, UUID reviewId){
        Like like = Like.builder()
                .userId(userId)
                .reviewId(reviewId)
                .build();

        likeRepository.save(like);
        reviewRepository.incrementLikeCount(reviewId);
        return new ReviewLikeDto(reviewId, userId, true);
    }

    private ReviewLikeDto cancelLike(Like like, UUID reviewId, UUID userId){
        likeRepository.delete(like);
        reviewRepository.decrementLikeCount(reviewId);
        return new ReviewLikeDto(reviewId, userId, false);
    }
}

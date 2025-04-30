package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewLikeDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Like;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.book.BookNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewAlreadyExistsException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewUnauthorizedException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
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

    private final CommentService commentService;
    private final NotificationService notificationService;

    @Transactional
    public ReviewDto create(ReviewCreateRequest request) {
        log.debug("리뷰 생성 시작: {}", request);
        if(reviewRepository.existsByUserIdAndBookId(request.userId(), request.bookId())) {
            throw ReviewAlreadyExistsException.withUserIdAndBookId(request.userId(), request.bookId());
        }
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException());
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException());

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
                .orElseThrow(() -> ReviewNotFoundException.withId(reviewId));
        log.info("리뷰 상세 조회 완료: id={}", reviewId);
        return ReviewMapper.toDto(review);
    }

    @Transactional
    public ReviewDto update(UUID userId, UUID reviewId, ReviewUpdateRequest request) {
        log.debug("리뷰 수정 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

        if(!review.isReviewer(userId)){
            throw ReviewUnauthorizedException.forUpdate(userId, reviewId);
        }
        review.update(request.content(), request.rating());
        log.info("리뷰 수정 완료: id={}", reviewId);
        return ReviewMapper.toDto(review);
    }

    @Transactional
    public void softDelete(UUID userId, UUID reviewId){
        log.debug("리뷰 논리 삭제 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

        if(!review.isReviewer(userId)){
            throw ReviewUnauthorizedException.forDelete(userId, reviewId);
        }
        commentService.softDeleteAllByReview(review);
        softDeleteAllLikesByReview(review);
        review.softDelete();
        log.info("리뷰 논리 삭제 완료: id={}", reviewId);
    }

    @Transactional
    public void softDeleteAllByBook(Book book) {
        List<Review> reviews = reviewRepository.findAllByBook(book);
        log.info("{}개의 리뷰를 논리 삭제 시작. bookId={}", reviews.size(), book.getId());
        reviews.forEach(review -> {
            review.softDelete();
            commentService.softDeleteAllByReview(review);
            softDeleteAllLikesByReview(review);
        });
        log.info("book 이 가진 모든 리뷰 논리 삭제 완료. bookId={}", book.getId());
    }

    @Transactional
    public void softDeleteAllByUser(User user) {
        List<Review> reviews = reviewRepository.findAllByUser(user);
        log.info("{}개의 리뷰를 논리 삭제 시작. userId={}", reviews.size(), user.getId());
        reviews.forEach(review -> {
            review.softDelete();
            commentService.softDeleteAllByReview(review);
            softDeleteAllLikesByReview(review);
        });
        log.info("사용자가 작성한 모든 리뷰 논리 삭제 완료. userId={}", user.getId());
    }

    @Transactional
    public void hardDelete(UUID userId, UUID reviewId){
        log.debug("리뷰 물리 삭제 시작: id={}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewNotFoundException.withId(reviewId));
        if(!review.isReviewer(userId)){
            throw ReviewUnauthorizedException.forDelete(userId, reviewId);
        }
        reviewRepository.delete(review);
        log.info("리뷰 물리 삭제 완료: id={}", reviewId);
    }

    @Transactional
    public ReviewLikeDto toggleLike(UUID reviewId, UUID userId){
        log.debug("좋아요 토글 요청. reviewId={}, userId={}", reviewId, userId);
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> ReviewNotFoundException.withId(reviewId));

        return likeRepository.findByReviewIdAndUserId(reviewId, userId)
                .map(like -> {
                    log.debug("기존 좋아요 존재. 좋아요 취소 시도 - reviewId={}, userId={}", reviewId, userId);
                    return cancelLike(like, reviewId, userId);
                })
                .orElseGet(() -> {
                    log.debug("기존 좋아요 없음. 좋아요 추가 시도 - reviewId={}, userId={}", reviewId, userId);

                    ReviewLikeDto result = addLike(userId, reviewId);

                    // 알림 생성
                    NotificationCreateRequest notificationRequest = NotificationCreateRequest.builder()
                        .type(NotificationType.REVIEW_COMMENTED)
                        .senderId(userId)
                        .reviewId(reviewId)
                        .build();
                    notificationService.create(notificationRequest);

                    return result;
                });
    }

    private ReviewLikeDto addLike(UUID userId, UUID reviewId){
        log.debug("좋아요 추가 시작. userId={}, reviewId={}", userId, reviewId);
        Like like = Like.builder()
                .userId(userId)
                .reviewId(reviewId)
                .build();
        likeRepository.save(like);
        reviewRepository.incrementLikeCount(reviewId);
        log.info("좋아요 추가 완료. userId={}, reviewId={}", userId, reviewId);
        return new ReviewLikeDto(reviewId, userId, true);
    }

    private ReviewLikeDto cancelLike(Like like, UUID reviewId, UUID userId){
        log.debug("좋아요 취소 시작. userId={}, reviewId={}", userId, reviewId);
        likeRepository.delete(like);
        reviewRepository.decrementLikeCount(reviewId);
        log.info("좋아요 취소 완료. userId={}, reviewId={}", userId, reviewId);
        return new ReviewLikeDto(reviewId, userId, false);
    }

    private void softDeleteAllLikesByReview(Review review) {
        List<Like> likes = likeRepository.findAllByReviewId(review.getId());
        log.debug("리뷰 연관 좋아요 논리 삭제 시작. reviewId={}, 총 {}건", review.getId(), likes.size());
        likes.forEach(Like::softDelete);
        log.info("리뷰 연관 좋아요 논리 삭제 완료. reviewId={}, 총 {}건", review.getId(), likes.size());
    }
}

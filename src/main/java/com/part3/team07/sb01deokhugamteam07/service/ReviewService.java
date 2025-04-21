package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.mapper.ReviewMapper;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ReviewService {

    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;

    public ReviewDto create(ReviewCreateRequest request){
        if(reviewRepository.existsByUserIdAndBookId(request.userId(), request.bookId())){
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

        return ReviewMapper.toDto(user, book, review);
    }
}

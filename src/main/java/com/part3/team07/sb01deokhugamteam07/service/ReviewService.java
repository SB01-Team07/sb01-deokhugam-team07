package com.part3.team07.sb01deokhugamteam07.service;


import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
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


        userRepository.findById(request.userId())
                .orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않습니다."));

        bookRepository.findById(request.bookId())
                .orElseThrow(()-> new IllegalArgumentException("책이 존재하지 않습니다."));



        return new ReviewDto(null,null,null,null,null,null,
                null,0,0,0,true,null,null);

    }
}

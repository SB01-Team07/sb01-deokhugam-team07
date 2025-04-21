package com.part3.team07.sb01deokhugamteam07.controller;


import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> create(@RequestBody @Valid ReviewCreateRequest request){
        ReviewDto fakeReviewDto = new ReviewDto(
                UUID.randomUUID(),                // id
                UUID.randomUUID(),                // bookId
                "fake-book-title",                // bookTitle
                "fake-thumbnail-url",             // bookThumbnailUrl
                UUID.randomUUID(),                // userId
                "fake-nickname",                  // userNickName
                "fake-content",                   // content
                5,                                // rating
                0,                                // likeCount
                0,                                // commentCount
                false,                            // likeByMe
                LocalDateTime.now(),              // createdAt
                LocalDateTime.now()               // updatedAt
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fakeReviewDto);
    }
}

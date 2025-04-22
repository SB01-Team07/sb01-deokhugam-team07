package com.part3.team07.sb01deokhugamteam07.controller;


import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> create(@RequestBody @Validated ReviewCreateRequest request) {
        log.info("리뷰 생성 요청: {}", request);
        ReviewDto createdReview = reviewService.create(request);
        log.debug("리뷰 생성 응답: {}", createdReview);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReview);
    }

    @GetMapping("{reviewId}")
    public ResponseEntity<ReviewDto> find(@PathVariable UUID reviewId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.find(reviewId));
    }
}

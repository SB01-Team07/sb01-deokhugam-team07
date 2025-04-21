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

@RequiredArgsConstructor
@RestController
@RequestMapping("api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> create(@RequestBody @Valid ReviewCreateRequest request){
        ReviewDto reviewDto = reviewService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewDto);
    }
}

package com.part3.team07.sb01deokhugamteam07.dto.review.request;

public record ReviewCreateRequest (
    String bookId,
    String userId,
    String content,
    int rating
){

}

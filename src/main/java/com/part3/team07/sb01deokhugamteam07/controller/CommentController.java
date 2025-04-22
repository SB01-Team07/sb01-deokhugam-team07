package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentDto> create(
      @RequestBody @Valid CommentCreateRequest createRequest
  ) {
    log.info("create comment request = {}", createRequest);
    CommentDto createdComment = commentService.create(createRequest);
    log.debug("create comment response = {}", createdComment);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdComment);
  }

}

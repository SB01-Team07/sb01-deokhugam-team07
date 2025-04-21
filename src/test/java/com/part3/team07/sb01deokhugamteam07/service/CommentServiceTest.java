package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewRepository reviewRepository;


  private UUID userId;
  private UUID reviewId;
  private UUID bookId;
  private UUID commentId;
  private User testUser;
  private Review testReview;
  private Book testBook;
  private Comment comment;
  private LocalDateTime fixedNow;

  @BeforeEach
  void setUp(){
    commentId = UUID.randomUUID();
    userId = UUID.randomUUID();
    reviewId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    fixedNow = LocalDateTime.now();

    testBook = new Book("testBook", "testAuthor", "testDescription",
        "testPublisher", LocalDate.now(), "978-89-123-4567-0",
        "http://example.com/thumbnail.jpg", 0, 0);
    ReflectionTestUtils.setField(testBook, "id", bookId);

    testUser = new User("testUser", "1234", "test@test.com");
    ReflectionTestUtils.setField(testUser, "id", userId);


    testReview = new Review(testUser, testBook, "test", 0, 2, 1);
    ReflectionTestUtils.setField(testReview, "id", reviewId);

    comment = Comment.builder()
        .user(testUser)
        .review(testReview)
        .content("test")
        .build();
    ReflectionTestUtils.setField(comment, "id", commentId);
    ReflectionTestUtils.setField(comment, "createdAt", fixedNow);
    ReflectionTestUtils.setField(comment, "updatedAt", fixedNow);

  }

  @Test
  @DisplayName("댓글 생성 성공")
  void createComment(){
    //given
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));
    given(commentRepository.save(any(Comment.class))).willReturn(comment);

    CommentCreateRequest createRequest = new CommentCreateRequest(
        reviewId,
        userId,
        comment.getContent()
    );

    //when
    CommentDto result = commentService.create(createRequest);

    //then
    assertThat(result).isNotNull();
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 생성 실패 - 유저 존재X")
  void createCommentFailByUserNotFound(){
    //given
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    CommentCreateRequest createRequest = new CommentCreateRequest(
        reviewId,
        userId,
        comment.getContent()
    );

    //when & then
    assertThatThrownBy(()-> commentService.create(createRequest))
        .isInstanceOf(NoSuchElementException.class); // 예외 추가 시 변경 예정

  }

  @Test
  @DisplayName("댓글 생성 실패 - 리뷰 존재X")
  void createCommentFailByReviewNotFound(){
    //given
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));
    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.empty());

    CommentCreateRequest createRequest = new CommentCreateRequest(
        reviewId,
        userId,
        comment.getContent()
    );

    //when & then
    assertThatThrownBy(()-> commentService.create(createRequest))
        .isInstanceOf(NoSuchElementException.class); // 예외 추가 시 변경 예정

  }


}
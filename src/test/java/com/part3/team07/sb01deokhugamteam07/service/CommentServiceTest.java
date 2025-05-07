package com.part3.team07.sb01deokhugamteam07.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.response.CursorPageResponseCommentDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentUnauthorizedException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.InvalidCommentQueryException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.mapper.CommentMapper;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

  @Mock
  private NotificationService notificationService;

  @Mock
  private CommentMapper commentMapper;

  private UUID userId;
  private UUID reviewId;
  private UUID bookId;
  private UUID commentId;
  private User testUser;
  private Review testReview;
  private Book testBook;
  private Comment comment;
  private CommentDto commentDto;
  private LocalDateTime fixedNow;

  @BeforeEach
  void setUp() {
    commentId = UUID.randomUUID();
    userId = UUID.randomUUID();
    reviewId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    fixedNow = LocalDateTime.now();

    testBook = new Book("testBook", "testAuthor", "testDescription",
        "testPublisher", LocalDate.now(), "978-89-123-4567-0",
        "http://example.com/thumbnail.jpg", 0,  BigDecimal.ZERO);
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

    commentDto = new CommentDto(
        commentId,
        reviewId,
        userId,
        testUser.getNickname(),
        comment.getContent(),
        fixedNow,
        fixedNow
    );

  }

  @Test
  @DisplayName("댓글 생성 성공")
  void createComment() {
    //given
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));
    given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));
    given(commentRepository.save(any(Comment.class))).willReturn(comment);
    given(commentMapper.toDto(any(Comment.class))).willReturn(commentDto);
    CommentCreateRequest createRequest = new CommentCreateRequest(
        reviewId,
        userId,
        comment.getContent()
    );

    //when
    CommentDto result = commentService.create(createRequest);

    //then
    assertThat(result).isNotNull();
    verify(commentRepository).save(argThat(savedComment ->
        savedComment.getContent().equals("test") &&
            savedComment.getUser().equals(testUser) &&
            savedComment.getReview().equals(testReview)
    ));
  }

  @Test
  @DisplayName("댓글 생성 실패 - 유저 존재X")
  void createCommentFailByUserNotFound() {
    //given
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    CommentCreateRequest createRequest = new CommentCreateRequest(
        reviewId,
        userId,
        comment.getContent()
    );

    //when & then
    assertThatThrownBy(() -> commentService.create(createRequest))
        .isInstanceOf(UserNotFoundException.class);

  }

  @Test
  @DisplayName("댓글 생성 실패 - 리뷰 존재X")
  void createCommentFailByReviewNotFound() {
    //given
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));
    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.empty());

    CommentCreateRequest createRequest = new CommentCreateRequest(
        reviewId,
        userId,
        comment.getContent()
    );

    //when & then
    assertThatThrownBy(() -> commentService.create(createRequest))
        .isInstanceOf(ReviewNotFoundException.class);

  }

  @Test
  @DisplayName("댓글 수정 성공")
  void updateComment() {
    //given
    String newContent = "updated content";
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));
    given(commentMapper.toDto(any(Comment.class))).willReturn(
        new CommentDto(
            commentId,
            reviewId,
            userId,
            testUser.getNickname(),
            newContent,
            fixedNow,
            fixedNow
        )
    );

    UUID requestUserId = testUser.getId();
    CommentUpdateRequest updateRequest = new CommentUpdateRequest(
        newContent
    );

    //when
    CommentDto result = commentService.update(commentId, requestUserId, updateRequest);

    //then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo(newContent);
  }

  @Test
  @DisplayName("댓글 수정 실패 - 권한없음")
  void updateCommentFailByUnauthorizedUser() {
    //given
    UUID otherUserId = UUID.randomUUID();
    String newContent = "updated content";
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(otherUserId)))
        .willReturn(Optional.of(new User("otherUser", "1234", "other@test.com")));

    CommentUpdateRequest updateRequest = new CommentUpdateRequest(
        newContent
    );

    //when & then
    assertThatThrownBy(() -> commentService.update(commentId, otherUserId, updateRequest))
        .isInstanceOf(CommentUnauthorizedException.class);

  }

  @Test
  @DisplayName("댓글 수정 실패 - 댓글 존재X")
  void updateCommentFailCommentNotFound() {
    //given
    String newContent = "updated content";
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.empty());

    CommentUpdateRequest updateRequest = new CommentUpdateRequest(
        newContent
    );

    //when & then
    assertThatThrownBy(() -> commentService.update(commentId, userId, updateRequest))
        .isInstanceOf(CommentNotFoundException.class);

  }

  @Test
  @DisplayName("댓글 상세 정보 조회 성공")
  void findComment() {
    //given
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.of(comment));
    given(commentMapper.toDto(any(Comment.class))).willReturn(commentDto);

    //when
    CommentDto result = commentService.find(commentId);

    //then
    assertThat(result).isEqualTo(commentDto);
  }

  @Test
  @DisplayName("댓글 상세 정보 조회 실패 - 댓글 존재X or 논리 삭제 상태")
  void findCommentFailCommentNotFound() {
    //given
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.empty());

    //when & then
    assertThatThrownBy(() -> commentService.find(commentId))
        .isInstanceOf(CommentNotFoundException.class);
  }

  @Test
  @DisplayName("댓글 논리 삭제 성공")
  void softDeleteComment() {
    //given
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));

    //when
    commentService.softDelete(commentId, userId);

    //then
    assertThat(comment.isDeleted()).isTrue();
    //verify(reviewRepository).decrementCommentCount(comment.getReview().getId()); //댓글 감소 메서드 호출 확인
  }

  @Test
  @DisplayName("댓글 논리 삭제 실패 - 댓글 존재X or 논리 삭제 상태")
  void softDeleteCommentFailCommentNotFound() {
    //given
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.empty());

    //when & then
    assertThatThrownBy(() -> commentService.softDelete(commentId, userId))
        .isInstanceOf(CommentNotFoundException.class);
  }

  @Test
  @DisplayName("댓글 논리 삭제 실패 - 권한없음")
  void softDeleteCommentFailByUnauthorizedUser() {
    //given
    UUID otherUserId = UUID.randomUUID();
    given(commentRepository.findByIdAndIsDeletedFalse(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(otherUserId)))
        .willReturn(Optional.of(new User("otherUser", "1234", "other@test.com")));

    //when & then
    assertThatThrownBy(() -> commentService.softDelete(commentId, otherUserId))
        .isInstanceOf(CommentUnauthorizedException.class);
  }

  @Test
  @DisplayName("리뷰에 달린 모든 댓글 논리 삭제 성공")
  void softDeleteAllCommentsByReview() {
    //given
    Comment c1 = Comment.builder().user(testUser).review(testReview).content("1").build();
    Comment c2 = Comment.builder().user(testUser).review(testReview).content("2").build();
    Comment comment1 = spy(c1);
    Comment comment2 = spy(c2);

    given(commentRepository.findAllByReviewAndIsDeletedFalse(eq(testReview)))
        .willReturn(List.of(comment1, comment2));

    //when
    commentService.softDeleteAllByReview(testReview);

    //then
    verify(commentRepository).findAllByReviewAndIsDeletedFalse(testReview);
    verify(comment1).softDelete();
    verify(comment2).softDelete();
  }

  @Test
  @DisplayName("댓글 물리 삭제 성공")
  void hardDeleteComment() {
    //given
    given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));

    //when
    commentService.hardDelete(commentId, userId);

    //then
    verify(commentRepository).delete(comment);
  }

  @Test
  @DisplayName("댓글 물리 삭제 실패 - 댓글 존재X ")
  void hardDeleteCommentFailCommentNotFound() {
    //given
    given(commentRepository.findById(eq(commentId))).willReturn(Optional.empty());

    //when & then
    assertThatThrownBy(() -> commentService.hardDelete(commentId, userId))
        .isInstanceOf(CommentNotFoundException.class);
  }

  @Test
  @DisplayName("댓글 물리 삭제 실패 - 권한없음")
  void hardDeleteCommentFailByUnauthorizedUser() {
    //given
    UUID otherUserId = UUID.randomUUID();
    given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(otherUserId)))
        .willReturn(Optional.of(new User("otherUser", "1234", "other@test.com")));

    //when & then
    assertThatThrownBy(() -> commentService.hardDelete(commentId, otherUserId))
        .isInstanceOf(CommentUnauthorizedException.class);
  }

  @Test
  @DisplayName("댓글 물리 삭제 - 이미 논리 삭제된 댓글은 commentCount 감소하지 않음")
  void hardDeleteCommentAlreadySoftDeleted() {
    // given
    comment.softDelete();
    given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(testUser));

    // when
    commentService.hardDelete(commentId, userId);

    // then
    verify(commentRepository).delete(comment);
    verify(reviewRepository, never()).decrementCommentCount(any());
  }


  @Test
  @DisplayName("댓글 목록 조회 성공 - hasNext: true")
  void findCommentsByReviewId_hasNextTrue() {
    //given
    LocalDateTime time = LocalDateTime.of(2025, 4, 25, 12, 0);
    int limit = 3;

    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(testReview));

    Comment c1 = new Comment(testUser, testReview, "c1"); // 가장 최신 댓글
    Comment c2 = new Comment(testUser, testReview, "c2");
    Comment c3 = new Comment(testUser, testReview, "c3");
    Comment c4 = new Comment(testUser, testReview, "c4"); // 가장 오래된 댓글

    LocalDateTime t1 = time.minusMinutes(1);
    LocalDateTime t2 = time.minusMinutes(2);
    LocalDateTime t3 = time.minusMinutes(3);
    LocalDateTime t4 = time.minusMinutes(4);

    ReflectionTestUtils.setField(c1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c2, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c3, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c4, "id", UUID.randomUUID());

    ReflectionTestUtils.setField(c1, "createdAt", t1);
    ReflectionTestUtils.setField(c2, "createdAt", t2);
    ReflectionTestUtils.setField(c3, "createdAt", t3);
    ReflectionTestUtils.setField(c4, "createdAt", t4);

    List<Comment> mockComments = List.of(c1, c2, c3, c4);

    //limit 3이기 때문에 최신 댓글에서 3번째 댓글(t3)의 값이 커서에 들어가야한다.
    given(commentRepository.findCommentByCursor(
        eq(testReview),
        eq("DESC"),
        eq(t3.toString()),
        eq(t3),
        eq(limit),
        eq("createdAt")
    )).willReturn(mockComments);

    CommentDto dto1 = new CommentDto(c1.getId(), reviewId, userId, testUser.getNickname(),
        c1.getContent(), c1.getCreatedAt(), c1.getCreatedAt());
    CommentDto dto2 = new CommentDto(c2.getId(), reviewId, userId, testUser.getNickname(),
        c2.getContent(), c2.getCreatedAt(), c2.getCreatedAt());
    CommentDto dto3 = new CommentDto(c3.getId(), reviewId, userId, testUser.getNickname(),
        c3.getContent(), c3.getCreatedAt(), c3.getCreatedAt());

    given(commentMapper.toDto(c1)).willReturn(dto1);
    given(commentMapper.toDto(c2)).willReturn(dto2);
    given(commentMapper.toDto(c3)).willReturn(dto3);

    //when
    CursorPageResponseCommentDto result = commentService.findCommentsByReviewId(
        reviewId,
        "DESC",
        t3.toString(),
        t3,
        limit
    );

    //then
    assertThat(result.content()).containsExactly(dto1, dto2, dto3);
    assertThat(result.size()).isEqualTo(3);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursor()).isEqualTo(c3.getCreatedAt().toString());
    assertThat(result.nextAfter()).isEqualTo(c3.getCreatedAt());
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - hasNext: False")
  void findCommentsByReviewId_hasNextFalse() {
    //given
    LocalDateTime time = LocalDateTime.of(2025, 4, 25, 12, 0);
    int limit = 3;

    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(testReview));

    Comment c1 = new Comment(testUser, testReview, "c1"); // 가장 최신 댓글
    Comment c2 = new Comment(testUser, testReview, "c2");
    Comment c3 = new Comment(testUser, testReview, "c3"); // 가장 오래된 댓글

    LocalDateTime t1 = time.minusMinutes(1);
    LocalDateTime t2 = time.minusMinutes(2);
    LocalDateTime t3 = time.minusMinutes(3);

    ReflectionTestUtils.setField(c1, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c2, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(c3, "id", UUID.randomUUID());

    ReflectionTestUtils.setField(c1, "createdAt", t1);
    ReflectionTestUtils.setField(c2, "createdAt", t2);
    ReflectionTestUtils.setField(c3, "createdAt", t3);

    List<Comment> mockComments = List.of(c1, c2, c3);

    //limit 3이기 때문에 최신 댓글에서 3번째 댓글(t3)의 값이 커서에 들어가야한다.
    given(commentRepository.findCommentByCursor(
        eq(testReview),
        eq("DESC"),
        eq(t3.toString()),
        eq(t3),
        eq(limit),
        eq("createdAt")
    )).willReturn(mockComments);

    CommentDto dto1 = new CommentDto(c1.getId(), reviewId, userId, testUser.getNickname(),
        c1.getContent(), c1.getCreatedAt(), c1.getCreatedAt());
    CommentDto dto2 = new CommentDto(c2.getId(), reviewId, userId, testUser.getNickname(),
        c2.getContent(), c2.getCreatedAt(), c2.getCreatedAt());
    CommentDto dto3 = new CommentDto(c3.getId(), reviewId, userId, testUser.getNickname(),
        c3.getContent(), c3.getCreatedAt(), c3.getCreatedAt());

    given(commentMapper.toDto(c1)).willReturn(dto1);
    given(commentMapper.toDto(c2)).willReturn(dto2);
    given(commentMapper.toDto(c3)).willReturn(dto3);

    //when
    CursorPageResponseCommentDto result = commentService.findCommentsByReviewId(
        reviewId,
        "DESC",
        t3.toString(),
        t3,
        limit
    );

    //then
    assertThat(result.content()).containsExactly(dto1, dto2, dto3);
    assertThat(result.size()).isEqualTo(3);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursor()).isNull();
    assertThat(result.nextAfter()).isNull();
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 정렬방향, 커서 정상값일때")
  void findCommentsByReviewId_validDirectionAndCursor() {
    //given
    String direction = "ASC";
    String cursor = LocalDateTime.of(2025, 4, 29, 12, 0, 0).toString();

    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(testReview));
    given(commentRepository.findCommentByCursor(
        any(), eq("ASC"), any(), any(), anyInt(), any())
    ).willReturn(List.of());

    //when
    CursorPageResponseCommentDto result = commentService.findCommentsByReviewId(
        reviewId,
        direction,
        cursor,
        null,
        10
    );

    //then
    assertThat(result.content()).isEmpty();
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 리뷰 존재X")
  void findCommentsByReviewIdFailReviewNotFound() {
    //given
    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.empty());
    //when & then
    assertThatThrownBy(() -> commentService.findCommentsByReviewId(
        reviewId,
        "DESC",
        null,
        null,
        3
    )).isInstanceOf(ReviewNotFoundException.class);
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 커서값이 정렬 조건과 다를경우")
  void findCommentsFailByInvalidCursor() {
    //given
    String invalidCursor = "not-a-datetime";

    //when & then
    assertThatThrownBy(() -> commentService.findCommentsByReviewId(
        reviewId,
        "DESC",
        invalidCursor,
        null,
        10
    ))
        .isInstanceOf(InvalidCommentQueryException.class)
        .hasMessageContaining("잘못된 커서 포맷입니다.");
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 잘못된 정렬 방향")
  void findCommentsFailByInvalidDirection() {
    //given
    String invalidDirection = "DDD";

    //when & then
    assertThatThrownBy(() -> commentService.findCommentsByReviewId(
        reviewId,
        invalidDirection,
        null,
        null,
        10
    ))
        .isInstanceOf(InvalidCommentQueryException.class)
        .hasMessageContaining("정렬 방향은 ASC 또는 DESC만 가능합니다.");
  }

}
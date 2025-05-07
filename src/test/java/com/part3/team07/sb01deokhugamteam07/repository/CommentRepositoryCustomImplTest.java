package com.part3.team07.sb01deokhugamteam07.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.part3.team07.sb01deokhugamteam07.config.QuerydslConfig;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.comment.InvalidCommentQueryException;
import com.part3.team07.sb01deokhugamteam07.repository.querydsl.CommentRepositoryCustomImpl;
import com.part3.team07.sb01deokhugamteam07.util.TitleNormalizer;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@EnableJpaAuditing
@Import({QuerydslConfig.class, TitleNormalizer.class})
@ActiveProfiles("test")
class CommentRepositoryCustomImplTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentRepositoryCustomImpl commentRepositoryCustom;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  private User testUser;
  private Review testReview;
  private Book testBook;
  private Comment comment1;
  private Comment comment2;
  private Comment comment3;
  private LocalDateTime fixedNow;

  @BeforeEach
  void setUp() {
    fixedNow = LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0);

    testBook = new Book("testBook", "testAuthor", "testDescription",
        "testPublisher", LocalDate.now(), "978-89-123-4567-0",
        "http://example.com/thumbnail.jpg", 0, BigDecimal.ZERO);
    bookRepository.save(testBook);

    testUser = new User("testUser", "1234", "test@test.com");
    userRepository.save(testUser);

    testReview = new Review(testUser, testBook, "test", 0, 2, 1);
    reviewRepository.save(testReview);

    comment1 = Comment.builder()
        .user(testUser)
        .review(testReview)
        .content("test1")
        .build();

    comment2 = Comment.builder()
        .user(testUser)
        .review(testReview)
        .content("test2")
        .build();

    comment3 = Comment.builder()
        .user(testUser)
        .review(testReview)
        .content("test3")
        .build();

    commentRepository.save(comment1);
    commentRepository.save(comment2);
    commentRepository.save(comment3);

    ReflectionTestUtils.setField(comment1, "createdAt", fixedNow); //가장 최신 댓글
    ReflectionTestUtils.setField(comment2, "createdAt", fixedNow.minusSeconds(1));
    ReflectionTestUtils.setField(comment3, "createdAt", fixedNow.minusSeconds(2)); //가장 오래전 댓글

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - cursor가 null 일 때 DESC 정렬 조회")
  void findCommentsCursorNullDESC() {
    //when
    List<Comment> comments = commentRepositoryCustom.findCommentByCursor(
        testReview,
        "DESC",
        null,
        null,
        10,
        "createdAt"
    );

    //then
    assertThat(comments).hasSize(3);
    assertThat(comments.get(0).getContent()).isEqualTo("test1");
    assertThat(comments.get(1).getContent()).isEqualTo("test2");
    assertThat(comments.get(2).getContent()).isEqualTo("test3");
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - cursor가 null 일 때 ASC 정렬 조회")
  void findCommentsCursorNullASC() {
    //when
    List<Comment> comments = commentRepositoryCustom.findCommentByCursor(
        testReview,
        "ASC",
        null,
        null,
        10,
        "createdAt"
    );

    //then
    assertThat(comments).hasSize(3);
    assertThat(comments.get(0).getContent()).isEqualTo("test3");
    assertThat(comments.get(1).getContent()).isEqualTo("test2");
    assertThat(comments.get(2).getContent()).isEqualTo("test1");
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - cursor가 빈 문자열일 때 DESC 정렬 조회")
  void findCommentsCursorBlankDESC() {
    //when
    List<Comment> comments = commentRepositoryCustom.findCommentByCursor(
        testReview,
        "DESC",
        "",
        null,
        10,
        "createdAt"
    );

    //then
    assertThat(comments).hasSize(3);
    assertThat(comments.get(0).getContent()).isEqualTo("test1");
    assertThat(comments.get(1).getContent()).isEqualTo("test2");
    assertThat(comments.get(2).getContent()).isEqualTo("test3");

  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - cursor가 빈 문자열일 때 ASC 정렬 조회")
  void findCommentsCursorBlankASC() {
    //when
    List<Comment> comments = commentRepositoryCustom.findCommentByCursor(
        testReview,
        "ASC",
        "",
        null,
        10,
        "createdAt"
    );

    //then
    assertThat(comments).hasSize(3);
    assertThat(comments.get(0).getContent()).isEqualTo("test3");
    assertThat(comments.get(1).getContent()).isEqualTo("test2");
    assertThat(comments.get(2).getContent()).isEqualTo("test1");
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - cursor가 존재할 때 DESC 정렬 조회")
  void findCommentsCursorExistDESC() {
    //when
    List<Comment> comments = commentRepositoryCustom.findCommentByCursor(
        testReview,
        "DESC",
        comment2.getCreatedAt().toString(),
        null,
        10,
        "createdAt"
    );

    //then
    assertThat(comments).hasSize(1);
    assertThat(comments.get(0).getContent()).isEqualTo("test3");
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - cursor가 존재할 때 ASC 정렬 조회")
  void findCommentsCursorExistASC() {
    //when
    List<Comment> comments = commentRepositoryCustom.findCommentByCursor(
        testReview,
        "ASC",
        comment2.getCreatedAt().toString(),
        null,
        10,
        "createdAt"
    );

    //then
    assertThat(comments).hasSize(1);
    assertThat(comments.get(0).getContent()).isEqualTo("test1");
  }

  @Test
  @DisplayName("댓글 목록 조회 실패 - 정렬 조건이 선택사항에 없을 경우")
  void findCommentsFailByInvalidOrderBy() {
    //given
    String invalidSortBy = "invalidField";

    //when & then
    assertThatThrownBy(() -> commentRepositoryCustom.findCommentByCursor(
        testReview,
        "DESC",
        null,
        null,
        10,
        invalidSortBy
    ))
        .isInstanceOf(InvalidCommentQueryException.class)
        .hasMessageContaining("지원하지 않는 정렬 필드입니다.");
  }

  @Test
  @DisplayName("리뷰에 달린 댓글 총 개수 계산 성공 - 댓글 존재O")
  void countByReview_WithComments() {
    // when
    long count = commentRepositoryCustom.countByReview(testReview);

    // then
    assertThat(count).isEqualTo(3);
  }
  @Test
  @DisplayName("리뷰에 달린 댓글 총 개수 계산 성공 - 댓글 존재X")
  void countByReview_WithoutComments() {
    // given
    Review newReview = new Review(testUser, testBook, "newReview", 0, 2, 1);
    reviewRepository.save(newReview);

    // when
    long count = commentRepositoryCustom.countByReview(newReview);

    // then
    assertThat(count).isEqualTo(0);
  }

  @Test
  @DisplayName("countCommentsByReviewIds - 리뷰 ID별로 삭제되지 않은 댓글 수를 정확히 집계한다")
  void countCommentsByReviewIds() {
    // given
    comment3.softDelete();
    commentRepository.save(comment3);

    em.flush();
    em.clear();

    // when
    Map<UUID, Long> result = commentRepository.countCommentsByReviewIds(List.of(testReview.getId()));

    // then
    assertThat(result.get(testReview.getId())).isEqualTo(2L); // 삭제된 댓글 제외
  }

}
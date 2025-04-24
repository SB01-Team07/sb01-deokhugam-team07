package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UUID userId;
    private UUID bookId;
    private UUID reviewId;
    private User user;
    private Book book;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        user = User.builder()
                .nickname("User")
                .email("user@abc.com")
                .password("user1234")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        book = Book.builder()
                .title("Book")
                .author("Author")
                .description("book_book")
                .publisher("Publisher")
                .publishDate(LocalDate.now())
                .isbn(UUID.randomUUID().toString())
                .thumbnailFileName("url")
                .reviewCount(0)
                .rating(0.0)
                .build();
        ReflectionTestUtils.setField(book, "id", bookId);

        review = Review.builder()
                .user(user)
                .book(book)
                .content("content")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        reviewDto = new ReviewDto(
                reviewId,
                bookId,
                book.getTitle(),
                book.getThumbnailFileName(),
                userId,
                user.getNickname(),
                review.getContent(),
                review.getRating(),
                review.getLikeCount(),
                review.getCommentCount(),
                false, // likeByMe 기본 false
                null, // createdAt
                null  // updatedAt
        );
    }


    @DisplayName("도서 별 1개의 리뷰만 등록할 수 있다.")
    @Test
    void review_crate_success() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId,"content", 5);

        given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
            Review saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", reviewId);
            return saved;
        });

        //when
        ReviewDto result = reviewService.create(request);

        //then
        assertThat(result).isEqualTo(reviewDto);
        verify(reviewRepository).save(any(Review.class));
    }

    @DisplayName("이미 리뷰가 존재하는 경우 생성에 실패한다")
    @Test
    void createReview_Fail_DuplicateReview() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "리뷰 내용", 5);

        given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(true);

        //when then
        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 도서에 대한 리뷰가 존재합니다.");
    }

    @DisplayName("존재하지 않는 유저로 리뷰 생성 시 실패한다")
    @Test
    void createReview_Fail_UserNotFound() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "리뷰 내용", 5);

        given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용가 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 책으로 리뷰 생성 시 실패한다")
    @Test
    void createReview_Fail_BookNotFound() {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest(bookId, userId, "리뷰 내용", 5);

        given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("책이 존재하지 않습니다.");
    }

    @DisplayName("리뷰 Id로 리뷰 상세조회를 할 수 있다.")
    @Test
    void find() {
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        ReviewDto result = reviewService.find(reviewId);

        //then
        assertThat(result).isEqualTo(reviewDto);
    }

    @DisplayName("리뷰 Id로 조회 시 존재하지 않으면 예외가 발생한다.")
    @Test
    void find_ReviewNotFound() {
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reviewService.find(reviewId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리뷰를 찾을 수 없습니다.");
    }

    @DisplayName("본인이 작성한 리뷰를 수정할 수 있다.")
    @Test
    void update() {
        //given
        ReviewUpdateRequest request = new ReviewUpdateRequest("수정한 내용", 3);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        ReviewDto result = reviewService.update(userId, reviewId, request);

        //then
        assertThat(result.content()).isEqualTo("수정한 내용");
        assertThat(result.rating()).isEqualTo(3);
    }

    @DisplayName("본인이 작성하지 않은 리뷰는 수정할 수 없다.")
    @Test
    void updateReview_ShouldFail_WhenUserIsNotAuthor() {
        //given
        UUID otherUserId = UUID.randomUUID();
        ReviewUpdateRequest request = new ReviewUpdateRequest("수정한 내용", 3);
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when then
        assertThatThrownBy(()-> reviewService.update(otherUserId, reviewId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("본인이 작성한 리뷰가 아닙니다.");
    }

    @DisplayName("존재하지 않은 리뷰는 수정할 수 없다")
    @Test
    void updateReview_ShouldFail_WhenReviewDoesNotExist() {
        //given
        UUID otherReviewId = UUID.randomUUID();
        ReviewUpdateRequest request = new ReviewUpdateRequest("수정한 내용", 3);

        //when then
        assertThatThrownBy(()-> reviewService.update(userId, otherReviewId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리뷰를 찾을 수 없습니다.");
    }

    @DisplayName("리뷰 논리 삭제를 할 수 있다.")
    @Test
    void softDelete() {
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        reviewService.softDelete(userId, reviewId);

        //then
        assertThat(review.isDeleted()).isTrue();
    }

    @DisplayName("존재하지 않은 리뷰는 논리 삭제할 수 없다.")
    @Test
    void softDelete_fail_whenUserIsNotAuthor() {
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reviewService.softDelete(userId, reviewId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("본인이 작성하지 않은 리뷰는 논리 삭제할 수 없다.")
    @Test
    void softDelete_fail_whenReviewNotFound() {
        //given
        UUID otherUserId = UUID.randomUUID();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when then
        assertThatThrownBy(() -> reviewService.softDelete(otherUserId, reviewId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("book 기준으로 모든 리뷰를 논리 삭제한다.")
    @Test
    void softDeleteAllByBook() {
        // given
        Review review1 = Review.builder()
                .user(user)
                .book(book)
                .content("리뷰1")
                .rating(4)
                .likeCount(0)
                .commentCount(0)
                .build();
        Review review2 = Review.builder()
                .user(user)
                .book(book)
                .content("리뷰2")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
        given(reviewRepository.findAllByBook(book)).willReturn(List.of(review1, review2));

        // when
        reviewService.softDeleteAllByBook(book);

        // then
        assertThat(review1.isDeleted()).isTrue();
        assertThat(review2.isDeleted()).isTrue();
    }

    @DisplayName("user 기준으로 모든 리뷰를 논리 삭제한다.")
    @Test
    void softDeleteAllByUser() {
        // given
        Review review1 = Review.builder()
                .user(user)
                .book(book)
                .content("리뷰1")
                .rating(4)
                .likeCount(0)
                .commentCount(0)
                .build();
        Review review2 = Review.builder()
                .user(user)
                .book(book)
                .content("리뷰2")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
        given(reviewRepository.findAllByUser(user)).willReturn(List.of(review1, review2));

        // when
        reviewService.softDeleteAllByUser(user);

        // then
        assertThat(review1.isDeleted()).isTrue();
        assertThat(review2.isDeleted()).isTrue();
    }


    @DisplayName("리뷰를 물리 삭제할 수 있다.")
    @Test
    void hardDelete() {
        //given
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        //when
        reviewService.hardDelete(userId, reviewId);

        //then
        verify(reviewRepository).delete(review);
    }
}
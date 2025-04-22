package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.review.ReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
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
                .thumbnailUrl("url")
                .review_count(0)
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
                book.getThumbnailUrl(),
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
}
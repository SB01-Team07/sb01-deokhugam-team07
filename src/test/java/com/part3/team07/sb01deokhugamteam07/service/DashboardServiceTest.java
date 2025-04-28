package com.part3.team07.sb01deokhugamteam07.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.dto.book.response.CursorPageResponsePopularBookDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.PopularReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.review.response.CursorPageResponsePopularReviewDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.PowerUserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.UserMetricsDTO;
import com.part3.team07.sb01deokhugamteam07.dto.user.response.CursorPageResponsePowerUserDto;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepositoryCustom;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock
  private DashboardRepositoryCustom dashboardRepositoryCustom;

  @Mock
  private DashboardRepository dashboardRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private DashboardService dashboardService;

  @Test
  @DisplayName("주간 파워 유저를 정상적으로 조회")
  void getPowerUserWeeklySuccess() {
    // 기본 조회 확인
    // given
    int limit = 10;
    Period period = Period.WEEKLY;
    UUID userId = UUID.randomUUID();

    // dashboardRepositoryCustom 반환 목 객체
    List<Dashboard> mockDashboards = List.of(
        new Dashboard(userId, KeyType.USER, period, 78.69999999999999,
            ValueType.SCORE, 1)
    );
    // userRepository 반환 목 객체
    User user = User.builder()
        .nickname("testUser")
        .password("password")
        .email("test@domain.com")
        .build();
    // private 필드 'id' 를 강제로 세팅
    ReflectionTestUtils.setField(user, "id", userId);
    List<User> mockUsers = List.of(
        user
    );

    // dashboardRepository 반환 목 객체
    List<UserMetricsDTO> mockUserMetrics = List.of(new UserMetricsDTO(userId,
        94.19999999999999,
        38.0,
        80.0));
    // content 으로 쓰이는 PowerUserDto 객체
    List<PowerUserDto> mockPowerUsers = List.of(
        new PowerUserDto(
            UUID.randomUUID(),
            "testUser1",
            period,
            LocalDateTime.now(),
            1,
            78.69999999999999,
            94.19999999999999,
            38,
            80
        )
    );

    // dashboardService 반환
    CursorPageResponsePowerUserDto cursorPageResponsePowerUserDto = new CursorPageResponsePowerUserDto(
        mockPowerUsers,
        null,
        null,
        10,
        1,
        false
    );

    when(userRepository.findAllById(List.of(userId))).thenReturn(mockUsers);
    when(dashboardRepositoryCustom.findPowerUsersByPeriod(
        eq(period), eq("ASC"), eq(null), eq(null), eq(limit + 1))
    ).thenReturn(mockDashboards);

    when(dashboardRepository.getUserMetrics(eq(period))).thenReturn(mockUserMetrics);

    when(dashboardRepository.countByKeyTypeAndPeriod(eq(KeyType.USER),
        eq(Period.WEEKLY))).thenReturn(1L);

    // when
    CursorPageResponsePowerUserDto result = dashboardService.getPowerUsers(
        period, // 랭킹 기간
        "ASC", // direction
        null, // cursor
        null, // atter
        limit // limit
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.content().get(0).rank()).isEqualTo(1);
  }


  @Test
  @DisplayName("주간 인기 리뷰를 정상적으로 조회")
  void getPopularReview() {
    // given
    int limit = 50; // Default value
    Period period = Period.WEEKLY;
    // 리뷰, 도서는 클라이언트에게 key 와 id 전부 응답시 전달되어야 합니다. *유저는 key 만
    UUID reviewId = UUID.randomUUID();
    UUID dashboardId = UUID.randomUUID();

    // dashboardRepositoryCustom 반환 목 객체
    Dashboard reviewDashboard = new Dashboard(reviewId, KeyType.REVIEW, period, 12.1,
        ValueType.SCORE, 1);
    ReflectionTestUtils.setField(reviewDashboard, "id", dashboardId);
    List<Dashboard> mockDashboards = List.of(reviewDashboard);

    // bookRepository 반환 목 객체
    Book book = Book.builder()
        .title("testBook")
        .thumbnailUrl("dummyUrl")
        .build();
    UUID bookId = UUID.randomUUID();
    ReflectionTestUtils.setField(book, "id", bookId);

    // userRepository 반환 목 객체
    User user = User.builder()
        .nickname("testUser")
        .build();
    UUID userId = UUID.randomUUID();
    ReflectionTestUtils.setField(user, "id", userId);

    // reviewRepository 반환 목 객체
    Review review = Review.builder()
        .content("test Content")
        .book(book)
        .user(user)
        .rating(5)
        .likeCount(10)
        .commentCount(5)
        .build();
    ReflectionTestUtils.setField(review, "id", reviewId);
    List<Review> reviews = List.of(review);

    // content 으로 쓰이는 PopularReviewDto 객체
    List<PopularReviewDto> mockPopularReviews = List.of(
        new PopularReviewDto(
            dashboardId,
            reviewId,
            bookId,
            book.getTitle(),
            book.getThumbnailUrl(),
            user.getId(),
            user.getNickname(),
            review.getContent(),
            review.getRating(),
            period,
            LocalDateTime.now(),
            1,
            12.1,
            review.getLikeCount(),
            review.getCommentCount()
        )
    );

    when(dashboardRepositoryCustom.findPopularReviewByPeriod(
        eq(period), eq("ASC"), eq(null), eq(null), eq(limit + 1)
    )).thenReturn(mockDashboards);
    when(reviewRepository.findAllById(any(List.class))).thenReturn(reviews);
    when(dashboardRepository.countByKeyTypeAndPeriod(eq(KeyType.REVIEW),
        eq(Period.WEEKLY))).thenReturn(1L);

    // when
    CursorPageResponsePopularReviewDto result = dashboardService.getPopularReview(
        period, // 랭킹 기간
        "ASC", // direction
        null, // cursor
        null, // atter
        limit // limit
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.content().get(0).rank()).isEqualTo(1);
  }

  @Test
  @DisplayName("주간 인기 도서를 정상적으로 조회")
  void getPowerBooksWeeklySuccess() {
    // given
    int limit = 50; // Default value
    Period period = Period.WEEKLY;

    // 리뷰, 도서는 클라이언트에게 key 와 id 전부 응답시 전달되어야 합니다. *유저는 key 만
    UUID bookId = UUID.randomUUID();
    UUID dashboardId = UUID.randomUUID();

    Dashboard bookDashboard = new Dashboard(bookId, KeyType.REVIEW, period, 12.1,
        ValueType.SCORE, 1);
    ReflectionTestUtils.setField(bookDashboard, "id", dashboardId);
    List<Dashboard> dashboards = List.of(bookDashboard);

    Book book = Book.builder()
        .title("testBook")
        .author("testAuthor")
        .description("test decription")
        .publishDate(LocalDate.now())
        .reviewCount(5)
        .rating(5)
        .thumbnailUrl("dummyUrl")
        .build();
    ReflectionTestUtils.setField(book, "id", bookId);
    List<Book> books = List.of(book);

    when(dashboardRepositoryCustom.findPopularBookByPeriod(
        eq(period), eq("ASC"), eq(null), eq(null), eq(limit + 1)
    )).thenReturn(dashboards);
    when(bookRepository.findAllById(any(List.class))).thenReturn(books);
    when(dashboardRepository.countByKeyTypeAndPeriod(eq(KeyType.BOOK), eq(period))).thenReturn(1L);

    //when
    CursorPageResponsePopularBookDto result = dashboardService.getPopularBooks(
        period, // 랭킹 기간
        "ASC", // direction
        null, // cursor
        null, // atter
        limit // limit
    );

    // then
    assertThat(result).isNotNull();
    assertThat(result.content().get(0).rank()).isEqualTo(1);
  }
}
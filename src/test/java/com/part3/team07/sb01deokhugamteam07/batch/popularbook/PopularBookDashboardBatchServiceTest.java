package com.part3.team07.sb01deokhugamteam07.batch.popularbook;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.part3.team07.sb01deokhugamteam07.batch.AssignRankUtil;
import com.part3.team07.sb01deokhugamteam07.batch.DateRangeUtil;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.entity.ValueType;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.DashboardRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PopularBookDashboardBatchServiceTest {

  @Mock
  private DateRangeUtil dataRangeUtil;
  @Mock
  private AssignRankUtil assignRank;
  @Mock
  private BookRepository bookRepository;
  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private DashboardRepository dashboardRepository;

  @InjectMocks
  private PopularBookDashboardBatchService popularBookDashboardBatchService;

  @Test
  @DisplayName("인기 도서 대시보드 데이터 저장 성공")
  void save_Popular_Book_Dashboard_Data_Success() {
    Period period = Period.WEEKLY;

    // Period 설정
    LocalDate[] dateRange = new LocalDate[]{
        LocalDate.of(2025, 4, 15),
        LocalDate.of(2025, 4, 21),
    };

    // 도서 설정
    UUID bookId1 = UUID.randomUUID();
    Book book1 = Book.builder()
        .title("test1")
        .author("autor1")
        .description("test decription")
        .publisher("publisher1")
        .publishDate(LocalDate.now())
        .reviewCount(5)
        .rating(BigDecimal.valueOf(4))
        .build();
    ReflectionTestUtils.setField(book1, "id", bookId1);
    UUID bookId2 = UUID.randomUUID();
    Book book2 = Book.builder()
        .title("test2")
        .author("autor2")
        .description("test decription")
        .publisher("publisher2")
        .publishDate(LocalDate.now())
        .reviewCount(10)
        .rating(BigDecimal.valueOf(5))
        .build();
    ReflectionTestUtils.setField(book2, "id", bookId2);
    List<Book> books = List.of(book1, book2);

    // 점수 계산을 위한 Review
    UUID reviewId1 = UUID.randomUUID();
    Review review1 = Review.builder()
        .content("test Content1")
        .book(book1)
        .user(User.builder().build())
        .rating(3)
        .likeCount(10)
        .commentCount(5)
        .build();
    ReflectionTestUtils.setField(review1, "id", reviewId1);
    UUID reviewId2 = UUID.randomUUID();
    Review review2 = Review.builder()
        .content("test Content2")
        .book(book2)
        .user(User.builder().build())
        .rating(5)
        .likeCount(50)
        .commentCount(10)
        .build();
    ReflectionTestUtils.setField(review2, "id", reviewId2);
    List<Review> reviews1 = List.of(review1);
    List<Review> reviews2 = List.of(review2);

    // 대시보드 설정
    List<Dashboard> dashboards = List.of(
        Dashboard.builder()
            .key(bookId2)
            .keyType(KeyType.BOOK)
            .period(period)
            .value(BigDecimal.valueOf(1 * 0.4 + 5 / 1 * 0.6))
            .valueType(ValueType.SCORE)
            .rank(1)
            .build(),
        Dashboard.builder()
            .key(bookId1)
            .keyType(KeyType.BOOK)
            .period(period)
            .value(BigDecimal.valueOf(1 * 0.4 + 3 / 1 * 0.6))
            .valueType(ValueType.SCORE)
            .rank(2)
            .build()
    );

    when(bookRepository.findByIsDeletedFalseOrderByCreatedAtAsc()).thenReturn(books);
    when(dataRangeUtil.getDateRange(period)).thenReturn(dateRange);
    when(reviewRepository.findByBookIdAndCreatedAtBetweenAndIsDeletedFalse(eq(bookId1),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(reviews1);
    when(reviewRepository.findByBookIdAndCreatedAtBetweenAndIsDeletedFalse(eq(bookId2),
        any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(reviews2);
    when(assignRank.assignRank(any(Map.class), eq(period), eq(KeyType.BOOK),
        any(List.class))).thenReturn(dashboards);

    // when
    popularBookDashboardBatchService.savePopularBookDashboardData(period);

    verify(bookRepository).findByIsDeletedFalseOrderByCreatedAtAsc();
    verify(reviewRepository, times(2)).findByBookIdAndCreatedAtBetweenAndIsDeletedFalse(any(),
        any(), any());
    verify(dashboardRepository).saveAll(any());
  }

  @Test
  public void when_No_Books_Exist() {
    when(bookRepository.findByIsDeletedFalseOrderByCreatedAtAsc()).thenReturn(List.of());
    popularBookDashboardBatchService.savePopularBookDashboardData(Period.DAILY);
    verify(dashboardRepository, never()).saveAll(anyList());
  }


}
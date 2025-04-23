package com.part3.team07.sb01deokhugamteam07.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.review.request.ReviewUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.BookRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

/*    @Autowired
    private CustomUserDetailsService customUserDetailsService;*/

    @Test
    @DisplayName("리뷰 생성 API 통합 테스트 - 성공")
    void createReview_Success() throws Exception {
        //given
        User user = User.builder()
                .nickname("user")
                .email("user@abc.com")
                .password("user1234")
                .build();
        userRepository.save(user);

        Book book = Book.builder()
                .title("Book")
                .author("Author")
                .description("Good book")
                .publisher("Publisher")
                .publishDate(LocalDate.now())
                .isbn("1234567890123")
                .thumbnailFileName("url")
                .reviewCount(0)
                .rating(0.0)
                .build();
        bookRepository.save(book);

        ReviewCreateRequest createRequest = new ReviewCreateRequest(
                book.getId(),
                user.getId(),
                "정말 좋은 책입니다.",
                5
        );

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookId").value(book.getId().toString()))
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.content").value("정말 좋은 책입니다."))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeByMe").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @DisplayName("리뷰 상세 조회 api 통합 테스트")
    @Test
    void findReview_Success() throws Exception {
        // Given
        User user = User.builder()
                .nickname("user")
                .email("user@abc.com")
                .password("user1234")
                .build();
        userRepository.save(user);

        Book book = Book.builder()
                .title("Book")
                .author("Author")
                .description("Description")
                .publisher("Publisher")
                .publishDate(LocalDate.now())
                .isbn("1234567890123")
                .thumbnailFileName("Url")
                .reviewCount(0)
                .rating(0.0)
                .build();
        bookRepository.save(book);

        Review review = Review.builder()
                .user(user)
                .book(book)
                .content("정말 좋은 책입니다.")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
        reviewRepository.save(review);

        // When & Then
        mockMvc.perform(get("/api/reviews/{reviewId}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId().toString()))
                .andExpect(jsonPath("$.bookId").value(book.getId().toString()))
                .andExpect(jsonPath("$.userId").value(user.getId().toString()))
                .andExpect(jsonPath("$.content").value("정말 좋은 책입니다."))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeByMe").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @DisplayName("리뷰 수정 API 테스트")
    @Test
    void update() throws Exception {
        //given
        User user = User.builder()
                .nickname("user")
                .email("user@abc.com")
                .password("user1234")
                .build();
        userRepository.save(user);

        Book book = Book.builder()
                .title("Book")
                .author("Author")
                .description("Description")
                .publisher("Publisher")
                .publishDate(LocalDate.now())
                .isbn("1234567890123")
                .thumbnailFileName("Url")
                .reviewCount(0)
                .rating(0.0)
                .build();
        bookRepository.save(book);

        Review review = Review.builder()
                .user(user)
                .book(book)
                .content("정말 좋은 책입니다.")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
        reviewRepository.save(review);

        ReviewUpdateRequest request = new ReviewUpdateRequest("변경 값", 3);

        //when then
        mockMvc.perform(patch("/api/reviews/{reviewId}", review.getId().toString())
                    .header("Deokhugam-Request-User-ID", user.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(request.content()))
                .andExpect(jsonPath("$.rating").value(request.rating()));
    }

    @DisplayName("리뷰 논리 삭제 api 통합 테스트")
    @Test
    void test() throws Exception {
        //given
        User user = User.builder()
                .nickname("user")
                .email("user@abc.com")
                .password("user1234")
                .build();
        userRepository.save(user);

        Book book = Book.builder()
                .title("Book")
                .author("Author")
                .description("Description")
                .publisher("Publisher")
                .publishDate(LocalDate.now())
                .isbn("1234567890123")
                .thumbnailFileName("Url")
                .reviewCount(0)
                .rating(0.0)
                .build();
        bookRepository.save(book);

        Review review = Review.builder()
                .user(user)
                .book(book)
                .content("정말 좋은 책입니다.")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
        reviewRepository.save(review);

        //when then
        mockMvc.perform(delete("/api/reviews/{reviewId}", review.getId())
                .header("Deokhugam-Request-User-ID", user.getId().toString()))
                .andExpect(status().isOk());
    }
}

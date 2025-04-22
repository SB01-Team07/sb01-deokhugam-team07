package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;


    @DisplayName("유저 ID와 책 ID로 리뷰 존재하면 ture를 반환한다.")
    @Test
    void existsByUserIdAndBookId_ExistingReview_ReturnsTrue() {
        //given
        User user = userRepository.save(createTestUser("testUser", "test@example.com"));
        Book book = bookRepository.save(createTestBook("Test Book"));
        Review review = reviewRepository.save(createTestReview(user, book));

        em.flush();
        em.clear();

        //when
        boolean exists = reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        //then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("없는 유저 ID와 책 ID 조합으로는 리뷰가 존재하지 않는다")
    void existsByUserIdAndBookId_NonExistingReview_ReturnsFalse() {
        // given
        User user = userRepository.save(createTestUser("testUser", "test@example.com"));
        Book book = bookRepository.save(createTestBook("Test Book"));
        //리뷰는 저장하지 않음

        em.flush();
        em.clear();

        // when
        boolean exists = reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        // then
        assertThat(exists).isFalse();
    }


    private User createTestUser(String nickname, String email) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password("password123!@#") // 테스트용 비밀번호
                .build();
    }

    private Book createTestBook(String title) {
        return Book.builder()
                .title(title)
                .author("test-author")
                .description("test-description")
                .publisher("test-publisher")
                .publishDate(LocalDate.of(2024, 4, 20))
                .isbn(UUID.randomUUID().toString())
                .thumbnailUrl("test-thumbnail-url")
                .review_count(0)
                .rating(0.0)
                .build();
    }

    private Review createTestReview(User user, Book book) {
        return Review.builder()
                .user(user)
                .book(book)
                .content("정말 좋은 책입니다!")
                .rating(5)
                .likeCount(0)
                .commentCount(0)
                .build();
    }

}
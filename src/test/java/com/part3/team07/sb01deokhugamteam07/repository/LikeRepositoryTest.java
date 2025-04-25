package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Like;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class LikeRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TestEntityManager em;

    @DisplayName("리뷰 ID와 유저 ID로 좋아요를 조회할 수 있다")
    @Test
    void findByReviewIdAndUserId() {
        //given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Like like = Like.builder()
                .reviewId(reviewId)
                .userId(userId)
                .build();
        likeRepository.save(like);

        //when
        Optional<Like> result = likeRepository.findByReviewIdAndUserId(reviewId, userId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getReviewId()).isEqualTo(reviewId);
    }

    @Test
    @DisplayName("없는 리뷰 ID + 유저 ID 조합으로 조회하면 Optional.empty()")
    void findByReviewIdAndUserId_notFound() {
        //given
        UUID otherReviewId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        //when
        Optional<Like> result = likeRepository.findByReviewIdAndUserId(otherReviewId, otherUserId);

        //then
        assertThat(result).isEmpty();
    }

}
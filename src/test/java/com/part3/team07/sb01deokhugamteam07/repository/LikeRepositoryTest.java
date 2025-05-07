package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.config.QuerydslConfig;
import com.part3.team07.sb01deokhugamteam07.entity.Like;
import com.part3.team07.sb01deokhugamteam07.util.TitleNormalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@Import({QuerydslConfig.class, TitleNormalizer.class})
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

    @DisplayName("리뷰 id로 모든 좋아요를 조회할 수 있다.")
    @Test
    void findAllByReviewId() {
        //given
        UUID reviewId = UUID.randomUUID();

        Like like1 = Like.builder()
                .userId(UUID.randomUUID())
                .reviewId(reviewId)
                .build();

        Like like2 = Like.builder()
                .userId(UUID.randomUUID())
                .reviewId(reviewId)
                .build();
        likeRepository.saveAll(List.of(like1, like2));

        em.flush();
        em.clear();

        //when
        List<Like> result = likeRepository.findAllByReviewId(reviewId);

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("reviewId")
                .containsOnly(reviewId);
    }

    @DisplayName("리뷰 ID와 유저 ID로 좋아요 존재 여부를 조회할 수 있다.")
    @Test
    void existsByReviewIdAndUserId() {
        // given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        likeRepository.save(new Like(userId, reviewId));

        em.flush();
        em.clear();

        // when
        boolean exists = likeRepository.existsByReviewIdAndUserId(reviewId, userId);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("countLikesByReviewIds - 리뷰 아이디를 받아 좋아요 수를 Map으로 리턴한다.")
    @Test
    void countLikesByReviewIds() {
        UUID reviewId1 = UUID.randomUUID();
        UUID reviewId2 = UUID.randomUUID();

        likeRepository.save(new Like(UUID.randomUUID(), reviewId1));
        likeRepository.save(new Like(UUID.randomUUID(), reviewId1));
        likeRepository.save(new Like(UUID.randomUUID(), reviewId2));

        em.flush();
        em.clear();

        Map<UUID, Long> result = likeRepository.countLikesByReviewIds(List.of(reviewId1, reviewId2));

        assertThat(result.get(reviewId1)).isEqualTo(2L);
        assertThat(result.get(reviewId2)).isEqualTo(1L);
    }
}
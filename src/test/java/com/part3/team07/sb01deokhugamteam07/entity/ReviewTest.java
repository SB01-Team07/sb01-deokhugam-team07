package com.part3.team07.sb01deokhugamteam07.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @DisplayName("리뷰 작성자의 ID가 전달된 ID와 같으면 true를 반환한다")
    @Test
    void returnsTrue_IfReviewer() {
        //given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .password("user12345678@")
                .nickname("nickname")
                .email("user@abc.com")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Review review = Review.builder()
                .user(user)
                .build();

        //when then
        assertThat(review.isReviewer(userId)).isTrue();
    }

    @DisplayName("리뷰 작성자의 ID가 전달된 ID가 다르면 false를 반환한다")
    @Test
    void ReturnsFalse_WhenUserIdDiffers() {
        //given
        UUID otherId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .password("user12345678@")
                .nickname("nickname")
                .email("user@abc.com")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Review review = Review.builder()
                .user(user)
                .build();

        //when then
        assertThat(review.isReviewer(otherId)).isFalse();
    }
}
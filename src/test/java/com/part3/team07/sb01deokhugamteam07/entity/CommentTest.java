package com.part3.team07.sb01deokhugamteam07.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

  @Test
  @DisplayName("댓글 수정 성공")
  void updateContentSuccess() {
    //given
    Comment comment = Comment.builder()
        .content("test")
        .build();
    //when
    comment.update("new test");

    //then
    assertThat(comment.getContent()).isEqualTo("new test");
  }

  @Test
  @DisplayName("댓글 수정 실패 - null값")
  void updateContentFailByNull() {
    //given
    Comment comment = Comment.builder()
        .content("test")
        .build();
    //when & then
    assertThatThrownBy(() -> comment.update(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("댓글 수정 실패 - Blank")
  void updateContentFailByBlank() {
    //given
    Comment comment = Comment.builder()
        .content("test")
        .build();
    //when & then
    assertThatThrownBy(() -> comment.update(""))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
package com.part3.team07.sb01deokhugamteam07.mapper;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  @Mapping(target = "reviewId", source = "review.id")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userNickname", source = "user.nickname")
  CommentDto toDto(Comment comment);

}

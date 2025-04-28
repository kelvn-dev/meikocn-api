package com.meikocn.api.mapping.rest;

import com.meikocn.api.dto.rest.request.CommentReqDto;
import com.meikocn.api.dto.rest.response.CommentResDto;
import com.meikocn.api.dto.rest.response.PageResDto;
import com.meikocn.api.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, ProjectMapper.class})
public interface CommentMapper {

  Comment dto2Model(CommentReqDto dto);

  CommentResDto model2Dto(Comment model);

  void updateModelFromDto(CommentReqDto dto, @MappingTarget Comment model);

  @Mapping(source = "totalElements", target = "totalItems")
  @Mapping(source = "number", target = "pageIndex")
  @Mapping(
      source = "content",
      target = "items",
      defaultExpression = "java(java.util.Collections.emptyList())")
  PageResDto<CommentResDto> model2Dto(Page<Comment> page);
}

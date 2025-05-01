package com.meikocn.api.mapping.rest;

import com.meikocn.api.dto.rest.request.TaskReqDto;
import com.meikocn.api.dto.rest.response.PageResDto;
import com.meikocn.api.dto.rest.response.TaskReminderResDto;
import com.meikocn.api.dto.rest.response.TaskResDto;
import com.meikocn.api.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, ProjectMapper.class, CommentMapper.class})
public interface TaskMapper {

  Task dto2Model(TaskReqDto dto);

  TaskResDto model2Dto(Task model);

  TaskReminderResDto model2ReminderDto(Task model);

  void updateModelFromDto(TaskReqDto dto, @MappingTarget Task model);

  @Mapping(source = "totalElements", target = "totalItems")
  @Mapping(source = "number", target = "pageIndex")
  @Mapping(
      source = "content",
      target = "items",
      defaultExpression = "java(java.util.Collections.emptyList())")
  PageResDto<TaskResDto> model2Dto(Page<Task> page);
}

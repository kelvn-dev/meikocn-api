package com.meikocn.api.mapping.rest;

import com.meikocn.api.dto.rest.request.ProjectReqDto;
import com.meikocn.api.dto.rest.response.PageResDto;
import com.meikocn.api.dto.rest.response.ProjectResDto;
import com.meikocn.api.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  Project dto2Model(ProjectReqDto dto);

  ProjectResDto model2Dto(Project user);

  void updateModelFromDto(ProjectReqDto dto, @MappingTarget Project dish);

  @Mapping(source = "totalElements", target = "totalItems")
  @Mapping(source = "number", target = "pageIndex")
  @Mapping(
      source = "content",
      target = "items",
      defaultExpression = "java(java.util.Collections.emptyList())")
  PageResDto<ProjectResDto> model2Dto(Page<Project> page);
}

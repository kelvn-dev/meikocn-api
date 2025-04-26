package com.meikocn.api.mapping.rest;

import com.meikocn.api.dto.rest.request.FileReqDto;
import com.meikocn.api.dto.rest.response.FileResDto;
import com.meikocn.api.dto.rest.response.PageResDto;
import com.meikocn.api.model.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface FileMapper {

  File dto2Model(FileReqDto dto);

  FileResDto model2Dto(File model);

  void updateModelFromDto(FileReqDto dto, @MappingTarget File model);

  @Mapping(source = "totalElements", target = "totalItems")
  @Mapping(source = "number", target = "pageIndex")
  @Mapping(
      source = "content",
      target = "items",
      defaultExpression = "java(java.util.Collections.emptyList())")
  PageResDto<FileResDto> model2Dto(Page<File> page);
}

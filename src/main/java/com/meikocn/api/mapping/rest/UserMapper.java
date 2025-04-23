package com.meikocn.api.mapping.rest;

import com.auth0.json.mgmt.permissions.Permission;
import com.meikocn.api.dto.rest.request.ProfileReqDto;
import com.meikocn.api.dto.rest.response.PageResDto;
import com.meikocn.api.dto.rest.response.UserResDto;
import com.meikocn.api.model.User;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

@Mapper(
    componentModel = "spring",
    uses = {PermissionMapper.class})
public interface UserMapper {

  User dto2Model(ProfileReqDto dto);

  UserResDto model2Dto(User user, Collection<GrantedAuthority> permissions);

  UserResDto model2Dto(User user, List<Permission> permissions);

  @Mapping(source = "totalElements", target = "totalItems")
  @Mapping(source = "number", target = "pageIndex")
  @Mapping(
      source = "content",
      target = "items",
      defaultExpression = "java(java.util.Collections.emptyList())")
  PageResDto<UserResDto> model2Dto(Page<User> users);

  void updateModelFromDto(ProfileReqDto dto, @MappingTarget User user);

  @Mapping(target = "avatar", source = "picture")
  @Mapping(
      target = "createdAt",
      expression = "java( user.getCreatedAt().toInstant().getEpochSecond() )")
  @Mapping(
      target = "updatedAt",
      expression = "java( user.getUpdatedAt().toInstant().getEpochSecond() )")
  User auth02Model(com.auth0.json.mgmt.users.User user);
}

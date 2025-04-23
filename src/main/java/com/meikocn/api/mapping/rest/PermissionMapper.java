package com.meikocn.api.mapping.rest;

import com.auth0.json.mgmt.permissions.Permission;
import com.meikocn.api.dto.rest.response.PermissionResDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "value", source = "name")
  @Mapping(target = "description", source = "description")
  PermissionResDto auth02Dto(Permission permission);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "value", source = "authority")
  @Mapping(target = "description", expression = "java(null)")
  PermissionResDto springAuthority2Dto(GrantedAuthority authority);
}

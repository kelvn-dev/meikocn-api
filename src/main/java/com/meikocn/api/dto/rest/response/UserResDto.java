package com.meikocn.api.dto.rest.response;

import java.util.List;
import lombok.Data;

@Data
public class UserResDto {
  private String id;
  private String nickname;
  private String email;
  private String avatar;
  private String languageCode;
  private List<PermissionResDto> permissions;
}

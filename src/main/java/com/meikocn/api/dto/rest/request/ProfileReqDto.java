package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileReqDto {
  @NotNull private String avatar;
  @NotNull private String nickname;
  private String languageCode;
}

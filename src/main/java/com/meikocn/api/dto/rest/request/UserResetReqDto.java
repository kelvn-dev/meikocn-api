package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserResetReqDto {
  @NotBlank
  @Size(min = 8)
  private String password;
}

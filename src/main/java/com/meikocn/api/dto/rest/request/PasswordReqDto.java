package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordReqDto {
  @NotBlank private String oldPassword;
  @NotBlank private String newPassword;
}

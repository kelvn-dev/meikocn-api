package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class UserReqDto {
  @NotBlank private String email;
  @NotNull private List<String> permissions;
}

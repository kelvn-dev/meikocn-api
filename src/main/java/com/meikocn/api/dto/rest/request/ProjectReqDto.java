package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectReqDto {
  @NotBlank private String name;
  private String description;
}

package com.meikocn.api.dto.rest.response;

import java.util.UUID;
import lombok.Data;

@Data
public class ProjectResDto {
  private UUID id;
  private String name;
  private String description;
}

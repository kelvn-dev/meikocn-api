package com.meikocn.api.dto.rest.response;

import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class ProjectResDto {
  private UUID id;
  private String name;
  private String description;
  private Long startDate;
  private Long endDate;
  private Integer progress;
  private Integer taskCount;
  private Integer doneTaskCount;
  private Set<String> userIds;
}

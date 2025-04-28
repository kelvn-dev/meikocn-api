package com.meikocn.api.dto.rest.request;

import com.meikocn.api.enums.TaskPriority;
import com.meikocn.api.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class TaskReqDto {
  @NotBlank private String name;
  private String description;
  private Long startDate;
  private Long endDate;
  private TaskPriority priority;
  private TaskStatus status;
  private String assigneeId;
  @NotNull private UUID projectId;
}

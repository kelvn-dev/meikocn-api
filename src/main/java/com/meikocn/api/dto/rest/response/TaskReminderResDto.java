package com.meikocn.api.dto.rest.response;

import com.meikocn.api.enums.TaskPriority;
import com.meikocn.api.enums.TaskStatus;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

@Data
public class TaskReminderResDto implements Serializable {
  private UUID id;
  private String name;
  private String description;
  private Long startDate;
  private Long endDate;
  private TaskPriority priority;
  private TaskStatus status;
  private UUID projectId;
  private ProjectResDto project;
}

package com.meikocn.api.repository;

import com.meikocn.api.enums.TaskStatus;
import com.meikocn.api.model.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends BaseRepository<Task, UUID> {
  Optional<Task> findByNameIgnoreCase(String name);

  int countByProjectIdAndCreatedAtBetweenAndStatus(
      UUID projectId, long start, long end, TaskStatus status);

  List<Task> getByProjectIdAndAssigneeId(UUID projectId, String assigneeId);

  List<Task> getByProjectId(UUID projectId);
}

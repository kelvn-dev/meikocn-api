package com.meikocn.api.repository;

import com.meikocn.api.model.Task;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends BaseRepository<Task, UUID> {
  Optional<Task> findByNameIgnoreCase(String name);
}

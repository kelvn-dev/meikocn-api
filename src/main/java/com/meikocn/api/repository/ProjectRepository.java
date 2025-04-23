package com.meikocn.api.repository;

import com.meikocn.api.model.Project;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends BaseRepository<Project, UUID> {
  Optional<Project> findByNameIgnoreCase(String name);
}

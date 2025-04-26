package com.meikocn.api.repository;

import com.meikocn.api.model.File;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends BaseRepository<File, UUID> {
  Optional<File> findByKeyIgnoreCase(String name);
}

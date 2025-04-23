package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.request.ProjectReqDto;
import com.meikocn.api.enums.*;
import com.meikocn.api.exception.ConflictException;
import com.meikocn.api.mapping.rest.ProjectMapper;
import com.meikocn.api.model.Project;
import com.meikocn.api.repository.ProjectRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends BaseService<Project, ProjectRepository> {

  private final ProjectMapper projectMapper;

  public ProjectService(ProjectRepository repository, ProjectMapper projectMapper) {
    super(repository);
    this.projectMapper = projectMapper;
  }

  public Project create(ProjectReqDto dto) {
    if (repository.findByNameIgnoreCase(dto.getName()).isPresent()) {
      throw new ConflictException(modelClass, "name", dto.getName());
    }
    Project project = projectMapper.dto2Model(dto);
    return repository.save(project);
  }

  public Project updateById(UUID id, ProjectReqDto dto) {
    Project project = this.getById(id, false);
    if (!project.getName().equalsIgnoreCase(dto.getName())) {
      if (repository.findByNameIgnoreCase(dto.getName()).isPresent()) {
        throw new ConflictException(modelClass, "name", dto.getName());
      }
    }
    projectMapper.updateModelFromDto(dto, project);
    return repository.save(project);
  }
}

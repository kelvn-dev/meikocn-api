package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.request.ProjectReqDto;
import com.meikocn.api.dto.rest.response.PageResDto;
import com.meikocn.api.dto.rest.response.ProjectResDto;
import com.meikocn.api.enums.*;
import com.meikocn.api.exception.ConflictException;
import com.meikocn.api.mapping.rest.ProjectMapper;
import com.meikocn.api.model.Project;
import com.meikocn.api.model.Task;
import com.meikocn.api.repository.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
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

  public ProjectResDto mapTaskData(Project project) {
    Set<Task> tasks = project.getTasks();
    int taskCount = tasks.size();
    int doneTaskCount =
        (int) tasks.stream().filter(t -> t.getStatus().equals(TaskStatus.DONE)).count();

    int progress = 0;
    if (taskCount != 0) {
      progress = (int) (((float) doneTaskCount / taskCount) * 100);
    }

    ProjectResDto dto = projectMapper.model2Dto(project);
    dto.setTaskCount(taskCount);
    dto.setDoneTaskCount(doneTaskCount);
    dto.setProgress(progress);
    return dto;
  }

  public PageResDto<ProjectResDto> mapTaskData(Page<Project> projectPage) {
    List<ProjectResDto> dtoList = new ArrayList<>();

    List<Project> projects = projectPage.getContent();
    projects.forEach(
        project -> {
          ProjectResDto dto = mapTaskData(project);
          dtoList.add(dto);
        });

    PageResDto<ProjectResDto> pageResDto = projectMapper.model2Dto(projectPage);
    pageResDto.setItems(dtoList);
    return pageResDto;
  }
}

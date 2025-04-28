package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.request.TaskReqDto;
import com.meikocn.api.exception.ConflictException;
import com.meikocn.api.mapping.rest.TaskMapper;
import com.meikocn.api.model.Task;
import com.meikocn.api.repository.TaskRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends BaseService<Task, TaskRepository> {

  private final TaskMapper taskMapper;

  public TaskService(TaskRepository repository, TaskMapper taskMapper) {
    super(repository);
    this.taskMapper = taskMapper;
  }

  public Task create(TaskReqDto dto) {
    if (repository.findByNameIgnoreCase(dto.getName()).isPresent()) {
      throw new ConflictException(modelClass, "name", dto.getName());
    }
    Task task = taskMapper.dto2Model(dto);
    return repository.save(task);
  }

  public Task updateById(UUID id, TaskReqDto dto) {
    Task task = this.getById(id, false);
    if (!task.getName().equalsIgnoreCase(dto.getName())) {
      if (repository.findByNameIgnoreCase(dto.getName()).isPresent()) {
        throw new ConflictException(modelClass, "name", dto.getName());
      }
    }
    taskMapper.updateModelFromDto(dto, task);
    return repository.save(task);
  }
}

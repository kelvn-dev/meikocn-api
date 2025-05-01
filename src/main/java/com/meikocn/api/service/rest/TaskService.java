package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.request.TaskReqDto;
import com.meikocn.api.exception.ConflictException;
import com.meikocn.api.mapping.rest.TaskMapper;
import com.meikocn.api.model.Task;
import com.meikocn.api.repository.TaskRepository;
import com.meikocn.api.service.scheduler.TaskScheduler;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService extends BaseService<Task, TaskRepository> {

  private final TaskMapper taskMapper;
  private final TaskScheduler taskScheduler;

  public TaskService(
      TaskRepository repository, TaskMapper taskMapper, TaskScheduler taskScheduler) {
    super(repository);
    this.taskMapper = taskMapper;
    this.taskScheduler = taskScheduler;
  }

  public Task create(TaskReqDto dto) {
    if (repository.findByNameIgnoreCase(dto.getName()).isPresent()) {
      throw new ConflictException(modelClass, "name", dto.getName());
    }
    Task task = taskMapper.dto2Model(dto);
    task = repository.save(task);

    taskScheduler.scheduleTaskReminder(task);

    return task;
  }

  public Task updateById(UUID id, TaskReqDto dto) {
    Task task = this.getById(id, false);
    if (!task.getName().equalsIgnoreCase(dto.getName())) {
      if (repository.findByNameIgnoreCase(dto.getName()).isPresent()) {
        throw new ConflictException(modelClass, "name", dto.getName());
      }
    }

    boolean requireReScheduleTaskReminder =
        !dto.getAssigneeId().equals(task.getAssigneeId())
            || !dto.getEndDate().equals(task.getEndDate());

    taskMapper.updateModelFromDto(dto, task);
    task = repository.save(task);

    if (requireReScheduleTaskReminder) {
      taskScheduler.reScheduleTaskReminder(task);
    }

    return task;
  }

  @Override
  public void deleteById(UUID id) {
    taskScheduler.cancelTaskReminder(id);
    super.deleteById(id);
  }
}

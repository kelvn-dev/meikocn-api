package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.response.TaskCountForEachUserResDto;
import com.meikocn.api.enums.TaskStatus;
import com.meikocn.api.model.Task;
import com.meikocn.api.repository.TaskRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticService {
  private final ProjectService projectService;
  private final TaskService taskService;
  private final TaskRepository taskRepository;

  public int getTaskCount(UUID projectId, long start, long end, TaskStatus taskStatus) {
    return taskRepository.countByProjectIdAndCreatedAtBetweenAndStatus(
        projectId, start, end, taskStatus);
  }

  public Map<String, Integer> getTaskCountForAllUser(UUID projectId) {
    List<Task> tasks = taskRepository.getByProjectId(projectId);
    Map<String, Integer> map = new HashMap<>();
    tasks.forEach(
        task -> map.put(task.getAssigneeId(), map.getOrDefault(task.getAssigneeId(), 0) + 1));

    return map;
  }

  public TaskCountForEachUserResDto getTaskCountForEachUser(UUID projectId, String userId) {
    List<Task> tasks = taskRepository.getByProjectIdAndAssigneeId(projectId, userId);
    AtomicInteger todo = new AtomicInteger();
    AtomicInteger inProgress = new AtomicInteger();
    AtomicInteger inReview = new AtomicInteger();
    AtomicInteger done = new AtomicInteger();
    tasks.forEach(
        task -> {
          switch (task.getStatus()) {
            case TODO -> todo.incrementAndGet();
            case IN_PROGRESS -> inProgress.incrementAndGet();
            case IN_REVIEW -> inReview.incrementAndGet();
            case DONE -> done.incrementAndGet();
          }
        });
    return new TaskCountForEachUserResDto(todo.get(), inProgress.get(), inReview.get(), done.get());
  }
}

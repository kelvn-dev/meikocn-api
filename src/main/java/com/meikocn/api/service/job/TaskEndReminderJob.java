package com.meikocn.api.service.job;

import com.meikocn.api.mapping.rest.TaskMapper;
import com.meikocn.api.model.Task;
import com.meikocn.api.model.TaskEntityGraph;
import com.meikocn.api.service.rest.TaskService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class TaskEndReminderJob implements Job {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    String taskId = jobDataMap.getString("taskId");
    String userId = jobDataMap.getString("userId");

    TaskEntityGraph entityGraph = TaskEntityGraph.____().project().____.____();
    Task task = taskService.getById(UUID.fromString(taskId), entityGraph, false);

    log.info("Sending end reminder to user {} about task {}", userId, taskId);
    simpMessagingTemplate.convertAndSend(
        String.format("/queue/users.%s.task-reminders", userId),
        taskMapper.model2ReminderDto(task));
  }
}

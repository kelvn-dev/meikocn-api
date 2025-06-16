package com.meikocn.api.service.job;

import com.meikocn.api.config.RabbitMQConfig;
import com.meikocn.api.model.Task;
import com.meikocn.api.model.TaskEntityGraph;
import com.meikocn.api.service.rest.TaskService;
import com.meikocn.api.service.rest.UserService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class TaskBeforeEndReminderJob implements Job {

  private final TaskService taskService;
  private final UserService userService;
  private final RabbitTemplate rabbitTemplate;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    String taskId = jobDataMap.getString("taskId");

    TaskEntityGraph entityGraph = TaskEntityGraph.____().project().____.assignee().____.____();
    Task task = taskService.getById(UUID.fromString(taskId), entityGraph, true);
    if (Objects.isNull(task)
        || Objects.isNull(task.getProject())
        || Objects.isNull(task.getAssignee())) {
      return;
    }

    log.info("Sending before-end reminder to user {} about task {}", task.getAssigneeId(), taskId);
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.TASK_REMINDER_EXCHANGE, RabbitMQConfig.TASK_REMINDER_ROUTINGKEY, task);
  }
}

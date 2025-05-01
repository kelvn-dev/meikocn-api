package com.meikocn.api.service.scheduler;

import com.meikocn.api.model.Task;
import com.meikocn.api.service.job.TaskReminderJob;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskScheduler {
  private final Scheduler scheduler;
  private static final String TASK_REMINDER_JOB_ID = "task-reminder-";
  private static final String TASK_REMINDER_TRIGGER_ID = "task-reminder-trigger-";

  @SneakyThrows
  public void scheduleTaskReminder(Task task) {
    String taskId = task.getId().toString();
    String userId = task.getAssigneeId();
    long endDate = task.getEndDate();

    JobDetail jobDetail =
        JobBuilder.newJob(TaskReminderJob.class)
            .withIdentity(TASK_REMINDER_JOB_ID + task)
            .usingJobData("taskId", taskId)
            .usingJobData("userId", userId)
            .build();
    Trigger trigger =
        TriggerBuilder.newTrigger()
            .withIdentity(TASK_REMINDER_TRIGGER_ID + taskId)
            .startAt(new Date(endDate * 1000))
            .build();
    scheduler.scheduleJob(jobDetail, trigger);
  }

  public void reScheduleTaskReminder(Task task) {
    cancelTaskReminder(task.getId());
    scheduleTaskReminder(task);
  }

  @SneakyThrows
  public void cancelTaskReminder(UUID taskId) {
    JobKey jobKey = JobKey.jobKey(TASK_REMINDER_JOB_ID + taskId);
    if (scheduler.checkExists(jobKey)) {
      scheduler.deleteJob(jobKey);
    }
  }
}

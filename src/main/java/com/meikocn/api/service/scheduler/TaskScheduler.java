package com.meikocn.api.service.scheduler;

import com.meikocn.api.model.Task;
import com.meikocn.api.service.job.TaskBeforeEndReminderJob;
import com.meikocn.api.service.job.TaskEndReminderJob;
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
  private static final String TASK_END_REMINDER_JOB_ID = "task-end-reminder-";
  private static final String TASK_END_REMINDER_TRIGGER_ID = "task-end-reminder-trigger-";
  private static final String TASK_BEFORE_END_REMINDER_JOB_ID = "task_before-end-reminder-";
  private static final String TASK_BEFORE_END_REMINDER_TRIGGER_ID =
      "task-before-end-reminder-trigger-";

  public void scheduleTaskReminder(Task task) {
    scheduleTaskEnd(task);
    scheduleTaskBeforeEnd(task);
  }

  public void reScheduleTaskReminder(Task task) {
    cancelTaskReminder(task.getId());
    scheduleTaskReminder(task);
  }

  @SneakyThrows
  public void cancelTaskReminder(UUID taskId) {
    JobKey endJobKey = JobKey.jobKey(TASK_END_REMINDER_JOB_ID + taskId);
    if (scheduler.checkExists(endJobKey)) {
      scheduler.deleteJob(endJobKey);
    }

    JobKey beforeEndJobKey = JobKey.jobKey(TASK_BEFORE_END_REMINDER_JOB_ID + taskId);
    if (scheduler.checkExists(beforeEndJobKey)) {
      scheduler.deleteJob(beforeEndJobKey);
    }
  }

  @SneakyThrows
  public void scheduleTaskEnd(Task task) {
    String taskId = task.getId().toString();
    String userId = task.getAssigneeId();
    long endDate = task.getEndDate();

    JobDetail jobDetail =
        JobBuilder.newJob(TaskEndReminderJob.class)
            .withIdentity(TASK_END_REMINDER_JOB_ID + taskId)
            .usingJobData("taskId", taskId)
            .usingJobData("userId", userId)
            .build();
    Trigger trigger =
        TriggerBuilder.newTrigger()
            .withIdentity(TASK_END_REMINDER_TRIGGER_ID + taskId)
            .startAt(new Date(endDate * 1000))
            .build();
    scheduler.scheduleJob(jobDetail, trigger);
  }

  @SneakyThrows
  public void scheduleTaskBeforeEnd(Task task) {
    String taskId = task.getId().toString();
    String userId = task.getAssigneeId();
    long endDate = task.getEndDate();

    JobDetail jobDetail =
        JobBuilder.newJob(TaskBeforeEndReminderJob.class)
            .withIdentity(TASK_BEFORE_END_REMINDER_JOB_ID + taskId)
            .usingJobData("taskId", taskId)
            .usingJobData("userId", userId)
            .build();
    Trigger trigger =
        TriggerBuilder.newTrigger()
            .withIdentity(TASK_BEFORE_END_REMINDER_TRIGGER_ID + taskId)
            .startAt(new Date((endDate - (3 * 24 * 60 * 60)) * 1000)) // 3 days ago
            .build();
    scheduler.scheduleJob(jobDetail, trigger);
  }
}

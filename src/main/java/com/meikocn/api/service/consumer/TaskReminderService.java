package com.meikocn.api.service.consumer;

import com.meikocn.api.config.RabbitMQConfig;
import com.meikocn.api.model.Task;
import com.meikocn.api.service.provider.SendgridService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskReminderService {
  private final SendgridService sendgridService;

  @RabbitListener(queues = RabbitMQConfig.TASK_REMINDER_QUEUE)
  private void sendEmail(Task task) {
    sendgridService.sendTaskReminderEmail(task, task.getAssignee());
    log.info("Sent email to {}", task.getAssignee().getEmail());
  }

  // TODO: Implement service to send task reminder SMS
  private void sendSms() {}
}

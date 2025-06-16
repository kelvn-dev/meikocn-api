package com.meikocn.api.service.consumer;

import com.meikocn.api.config.RabbitMQConfig;
import com.meikocn.api.model.Task;
import com.meikocn.api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FallBackService {
  @RabbitListener(queues = {RabbitMQConfig.CONFIRMMATION_INVITATION_FALLBACK_QUEUE})
  public void onRegistrationFailure(User user) {
    log.info("Executing fallback for failed confirmation invitation email {}", user);
  }

  @RabbitListener(queues = {RabbitMQConfig.TASK_REMINDER_FALLBACK_QUEUE})
  public void onTaskReminderFailure(Task task) {
    log.info(
        "Executing fallback for failed sending reminder to user {} about task {}",
        task.getAssignee(),
        task);
  }
}

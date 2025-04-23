package com.meikocn.api.service.listener;

import com.meikocn.api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FallBackService {
  @RabbitListener(queues = {"q.fallback-confirmation-invitation-email"})
  public void onRegistrationFailure(User user) {
    log.info("Executing fallback for failed confirmation invitation email {}", user);
  }
}

package com.meikocn.api.service.consumer;

import com.meikocn.api.model.User;
import com.meikocn.api.service.provider.SendgridService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationService {
  private final SendgridService sendgridService;

  @RabbitListener(queues = "q.confirmation-invitation-email")
  private void sendEmail(User user) {
    sendgridService.sendConfirmationInvitationEmail(user);
    log.info("Sent email to {}", user.getEmail());
  }

  // TODO: Implement service to send confirmation SMS
  private void sendSms() {}
}

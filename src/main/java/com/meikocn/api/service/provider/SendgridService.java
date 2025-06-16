package com.meikocn.api.service.provider;

import com.meikocn.api.config.ClientConfig;
import com.meikocn.api.config.SendgridConfig;
import com.meikocn.api.enums.SendGridTemplate;
import com.meikocn.api.model.Task;
import com.meikocn.api.model.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendgridService {

  private final SendgridConfig sendgridConfig;
  private final ClientConfig clientConfig;
  private final SendGrid sendGrid;
  private static final String sendgridDateFormat = "dddd, MMMM DD, YYYY";

  @SneakyThrows
  public void send(
      String emailTo, SendGridTemplate sendGridTemplate, Map<String, Object> templateData) {
    // Use Single Sender Verification configured in setting
    Email sender = new Email(sendgridConfig.getEmailSender());
    Email receiver = new Email(emailTo);
    Content content = new Content("text/html", "Empty");

    /**
     * known issue
     * https://stackoverflow.com/questions/53111157/sendgrid-v3-substitutions-may-not-be-used-with-dynamic-templating
     */
    Mail mail = new Mail(sender, null, receiver, content);
    mail.setReplyTo(sender);
    mail.setTemplateId(sendGridTemplate.getTemplateId());
    templateData.forEach(
        (key, value) -> mail.personalization.get(0).addDynamicTemplateData(key, value));

    Request request = new Request();
    Response response;
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      response = sendGrid.api(request);
      if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
        return;
      }
      throw new ServiceUnavailableException(response.getBody());
    } catch (IOException e) {
      throw new ServiceUnavailableException(e.getMessage());
    }
  }

  public void sendConfirmationInvitationEmail(User user) {
    Map<String, Object> templateData = new HashMap<>();
    templateData.put("name", user.getNickname());
    templateData.put("subject", "Invitation from Admin portal");
    templateData.put("dealer", "Admin portal");
    templateData.put(
        "activeLink",
        String.format("%s/%s", clientConfig.getAccountResetUrl(), user.getInviteToken()));
    send(user.getEmail(), SendGridTemplate.INVITATION_TEMPLATE, templateData);
  }

  public void sendTaskReminderEmail(Task task, User user) {
    Map<String, Object> templateData = new HashMap<>();
    templateData.put("user", user.getNickname());
    templateData.put("project", task.getProject().getName());
    templateData.put("task", task.getName());
    templateData.put("date", task.getEndDate());
    templateData.put("dateFormat", sendgridDateFormat);
    templateData.put("priority", task.getPriority().toString());
    templateData.put(
        "link", String.format("%s/%s", clientConfig.getTaskDetailUrl(), task.getId().toString()));
    send(user.getEmail(), SendGridTemplate.TASK_REMINDER_TEMPLATE, templateData);
  }
}
// Tuesday, June 18, 2025

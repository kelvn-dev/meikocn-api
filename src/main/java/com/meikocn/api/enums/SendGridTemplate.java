package com.meikocn.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SendGridTemplate {
  INVITATION_TEMPLATE("d-966ac34b747a4e35831b549faef64965"),
  TASK_REMINDER_TEMPLATE("d-08d3acc67761494ab444c82a6a8579a9");

  private String templateId;
}

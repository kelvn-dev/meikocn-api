package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentReqDto {
  @NotBlank private String content;
  @NotNull private String userId;
  @NotNull private String taskId;
}

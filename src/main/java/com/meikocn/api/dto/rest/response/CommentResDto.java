package com.meikocn.api.dto.rest.response;

import java.util.UUID;
import lombok.Data;

@Data
public class CommentResDto {
  private UUID id;
  private String content;
  private String userId;
}

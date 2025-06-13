package com.meikocn.api.dto.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskCountForEachUserResDto {
  private int todo;
  private int inProgress;
  private int inReview;
  private int done;
}

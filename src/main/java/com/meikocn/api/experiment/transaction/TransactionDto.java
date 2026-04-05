package com.meikocn.api.experiment.transaction;

import lombok.Data;

@Data
public class TransactionDto {
  private String value;
  private Boolean throwExceptionOutsideBefore;
  private Boolean throwExceptionOutsideAfter;
  private Boolean throwExceptionInside;
}

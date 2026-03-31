package com.meikocn.api.service.transaction;

import lombok.Data;

@Data
public class TransactionDto {
  private String value;
  private Boolean throwExceptionOutsideBefore;
  private Boolean throwExceptionOutsideAfter;
  private Boolean throwExceptionInside;
}

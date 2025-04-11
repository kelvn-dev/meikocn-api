package com.meikocn.api.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ApiValidationError extends ApiSubError {
  private String field;
  private Object rejectedValue;
  private String message;

  ApiValidationError(String message) {
    this.message = message;
  }
}

package com.meikocn.api.exception;

public class ForbiddenException extends BaseException {

  public ForbiddenException() {}

  public ForbiddenException(String message) {
    super(message);
  }
}

package com.meikocn.api.exception;

public class UnauthorizedException extends BaseException {

  public UnauthorizedException() {}

  public UnauthorizedException(String message) {
    super(message);
  }
}

package com.meikocn.api.exception;

public class VinposException extends RuntimeException {
  public VinposException() {}

  public VinposException(String message) {
    super(message);
  }

  public VinposException(Throwable cause) {
    super(cause);
  }

  public VinposException(String message, Throwable cause) {
    super(message, cause);
  }
}

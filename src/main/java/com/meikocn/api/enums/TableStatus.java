package com.meikocn.api.enums;

public enum TableStatus {
  AVAILABLE("available"),
  OCCUPIED("occupied"),
  RESERVED("reserved");

  private String value;

  private TableStatus(String value) {
    this.value = value;
  }

  public String toString() {
    return String.valueOf(this.value);
  }
}

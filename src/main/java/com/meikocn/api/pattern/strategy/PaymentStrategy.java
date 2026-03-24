package com.meikocn.api.pattern.strategy;

public interface PaymentStrategy {
  String pay(double amount);

  PaymentType getType();
}

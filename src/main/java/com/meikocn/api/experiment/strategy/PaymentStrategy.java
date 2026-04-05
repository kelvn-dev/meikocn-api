package com.meikocn.api.experiment.strategy;

public interface PaymentStrategy {
  String pay(double amount);

  PaymentType getType();
}

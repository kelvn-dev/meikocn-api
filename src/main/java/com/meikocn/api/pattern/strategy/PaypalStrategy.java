package com.meikocn.api.pattern.strategy;

import org.springframework.stereotype.Component;

@Component
public class PaypalStrategy implements PaymentStrategy {
  @Override
  public String pay(double amount) {
    return "PaypalStrategy";
  }

  @Override
  public PaymentType getType() {
    return PaymentType.PAYPAL;
  }
}

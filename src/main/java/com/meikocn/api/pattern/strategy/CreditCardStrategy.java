package com.meikocn.api.pattern.strategy;

import org.springframework.stereotype.Component;

@Component
public class CreditCardStrategy implements PaymentStrategy {
  @Override
  public String pay(double amount) {
    return "CreditCardStrategy";
  }

  @Override
  public PaymentType getType() {
    return PaymentType.CREDIT_CARD;
  }
}

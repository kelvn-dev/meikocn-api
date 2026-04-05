package com.meikocn.api.experiment.strategy;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
  private final PaymentStrategyFactory paymentStrategyFactory;

  public PaymentService(PaymentStrategyFactory paymentStrategyFactory) {
    this.paymentStrategyFactory = paymentStrategyFactory;
  }

  public String process(PaymentType type, double amount) {
    PaymentStrategy strategy = paymentStrategyFactory.getStrategy(type);
    return strategy.pay(amount);
  }
}

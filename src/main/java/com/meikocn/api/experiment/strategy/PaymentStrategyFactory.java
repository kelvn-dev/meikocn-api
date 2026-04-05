package com.meikocn.api.experiment.strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PaymentStrategyFactory {
  private final Map<PaymentType, PaymentStrategy> strategyMap;

  public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
    strategyMap =
        strategies.stream()
            .collect(
                Collectors.toMap(
                    PaymentStrategy::getType,
                    Function.identity(),
                    (a, b) -> a,
                    () -> new EnumMap<>(PaymentType.class)));
  }

  public PaymentStrategy getStrategy(PaymentType type) {
    return strategyMap.get(type);
  }
}

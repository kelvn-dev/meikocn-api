package com.meikocn.api.pattern.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/experiments")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @GetMapping("/payment")
  public ResponseEntity<String> process(
      @RequestParam("type") PaymentType paymentType, @RequestParam("amount") Double amount) {
    return ResponseEntity.ok(paymentService.process(paymentType, amount));
  }
}

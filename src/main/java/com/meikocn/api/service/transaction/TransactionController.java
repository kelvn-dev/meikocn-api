package com.meikocn.api.service.transaction;

import com.meikocn.api.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/experiments")
@RequiredArgsConstructor
public class TransactionController {
  private final OuterTransactionService transactionService;

  @PutMapping("/transaction")
  public ResponseEntity<String> update(@RequestBody @Valid TransactionDto dto)
      throws BadRequestException {
    transactionService.updateUser(dto);
    return ResponseEntity.ok(null);
  }
}

package com.meikocn.api.experiment.lock;

import com.meikocn.api.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/experiments")
@RequiredArgsConstructor
public class LockController {

  private final LockService lockService;

  @PutMapping("/optimistic-lock")
  public ResponseEntity<String> update(@RequestBody @Valid LockDto dto) throws BadRequestException {
    lockService.updateUser(dto);
    return ResponseEntity.ok(null);
  }

  @PutMapping("/pessimistic-read-read-lock")
  public ResponseEntity<String> readRead() throws BadRequestException {
    lockService.GetUserWithPessimisticReadLock();
    lockService.GetUserWithPessimisticReadLock();
    return ResponseEntity.ok(null);
  }

  @PutMapping("/pessimistic-read-write-lock")
  public ResponseEntity<String> readWrite() throws BadRequestException {
    lockService.GetUserWithPessimisticReadLock();
    lockService.GetUserWithPessimisticWriteLock();
    return ResponseEntity.ok(null);
  }

  @PutMapping("/pessimistic-write-read-lock")
  public ResponseEntity<String> writeRead() throws BadRequestException {
    lockService.GetUserWithPessimisticWriteLock();
    lockService.GetUserWithPessimisticReadLock();
    return ResponseEntity.ok(null);
  }

  @PutMapping("/pessimistic-write-write-lock")
  public ResponseEntity<String> writeWrite() throws BadRequestException {
    lockService.GetUserWithPessimisticWriteLock();
    lockService.GetUserWithPessimisticWriteLock();
    return ResponseEntity.ok(null);
  }
}

package com.meikocn.api.experiment.lock;

import com.meikocn.api.exception.NotFoundException;
import com.meikocn.api.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {
  private final LockUserRepository userRepository;

  @Transactional
  public void updateUser(LockDto dto) {
    String userId = "auth0|680fe9b15e50be9b026f7e13";
    User user =
        userRepository
            .findByIdWithOptimisticForceIncrementLock(userId)
            .orElseThrow(() -> new NotFoundException(User.class, "id", userId));

    user.setNickname(dto.getUserNickname());
    userRepository.save(user);
  }

  @Transactional
  @Async("generalTaskExecutor")
  public void GetUserWithPessimisticWriteLock() {
    try {
      String userId = "auth0|680fe9b15e50be9b026f7e13";
      userRepository
          .findByIdWithPessimisticWriteLock(userId)
          .orElseThrow(() -> new NotFoundException(User.class, "id", userId));
      log.info("Acquired write lock successfully");

      Thread.sleep(3000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    log.info("Released write lock successfully");
  }

  @Transactional
  @Async("generalTaskExecutor")
  public void GetUserWithPessimisticReadLock() {
    try {
      String userId = "auth0|680fe9b15e50be9b026f7e13";
      userRepository
          .findByIdWithPessimisticReadLock(userId)
          .orElseThrow(() -> new NotFoundException(User.class, "id", userId));
      log.info("Acquired read lock successfully");

      Thread.sleep(3000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    log.info("Released read lock successfully");
  }
}

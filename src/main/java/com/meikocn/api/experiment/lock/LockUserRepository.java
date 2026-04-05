package com.meikocn.api.experiment.lock;

import com.meikocn.api.model.User;
import com.meikocn.api.repository.BaseRepository;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface LockUserRepository extends BaseRepository<User, String> {
  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findByIdWithOptimisticForceIncrementLock(String id);

  @Lock(LockModeType.PESSIMISTIC_READ)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findByIdWithPessimisticReadLock(String id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findByIdWithPessimisticWriteLock(String id);
}

package com.meikocn.api.repository;

import com.meikocn.api.model.User;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, String> {
  Optional<User> findByEmail(String email);

  Optional<User> findByInviteToken(String inviteToken);
}

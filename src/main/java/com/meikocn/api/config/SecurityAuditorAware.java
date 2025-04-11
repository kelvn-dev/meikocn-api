package com.meikocn.api.config;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityAuditorAware implements AuditorAware<String> {

  @Override
  public @NotNull Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
      return Optional.empty();
    }
    return Optional.ofNullable(authentication.getName());
  }
}

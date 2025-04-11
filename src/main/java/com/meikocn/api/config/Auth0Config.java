package com.meikocn.api.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth0")
public class Auth0Config {
  private String domain;
  private String dbConnection;
  private String managementAPI;
  private UserManagement userManagement;

  @Getter
  @Setter
  public static class UserManagement {
    private String clientId;
    private String clientSecret;
  }
}

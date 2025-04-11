package com.meikocn.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.oauth2")
public class OAuth2Config {
  private boolean bypassed;
  private String issuerUri;
  private String jwkSetUri;
  private String audience;
}

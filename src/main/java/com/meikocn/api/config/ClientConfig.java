package com.meikocn.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientConfig {
  private String baseUrl;
  private String accountResetUrl;
  private String taskDetailUrl;
}

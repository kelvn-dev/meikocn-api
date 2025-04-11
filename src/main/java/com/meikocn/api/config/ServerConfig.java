package com.meikocn.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "server")
public class ServerConfig {
  private String port;
  private String defaultPassword;
}

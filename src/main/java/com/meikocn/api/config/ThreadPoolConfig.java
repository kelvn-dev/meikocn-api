package com.meikocn.api.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "async")
public class ThreadPoolConfig {
  private ThreadPool generalThreadPool;
  private ThreadPool s3ThreadPool;

  @Getter
  @Setter
  public static class ThreadPool {
    private Integer minimumSize;
    private Integer maximumSize;
    private Integer queueCapacity;
  }
}

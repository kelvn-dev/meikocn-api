package com.meikocn.api.config;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
  private final ThreadPoolConfig threadPoolConfig;

  @Bean
  public Executor generalTaskExecutor() {
    return createExecutor("general-upload-async", threadPoolConfig.getGeneralThreadPool());
  }

  private static Executor createExecutor(
      final String name, final ThreadPoolConfig.ThreadPool config) {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(config.getMinimumSize());
    executor.setMaxPoolSize(config.getMaximumSize());
    executor.setQueueCapacity(config.getQueueCapacity());
    executor.setThreadNamePrefix(String.format("%s-thread-", name));
    executor.initialize();
    return new DelegatingSecurityContextAsyncTaskExecutor(executor);
  }
}

package com.meikocn.api.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AWSPropConfig {
  private String keyId;
  private String secretKey;
  private String region;
  private S3 s3;

  @Getter
  @Setter
  public static class S3 {
    private String bucket;
    private String prefix;
  }
}

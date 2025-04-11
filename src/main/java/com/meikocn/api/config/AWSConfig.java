package com.meikocn.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Getter
@RequiredArgsConstructor
public class AWSConfig {

  private final AWSPropConfig awsPropConfig;

  @Bean
  public AwsCredentialsProvider awsCredentialsProvider() {
    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(awsPropConfig.getKeyId(), awsPropConfig.getSecretKey()));
  }

  @Bean
  public Region region() {
    return Region.of(awsPropConfig.getRegion());
  }

  @Bean
  public S3Client s3Client(Region region, AwsCredentialsProvider awsCredentialsProvider) {
    S3ClientBuilder s3ClientBuilder =
        S3Client.builder().region(region).credentialsProvider(awsCredentialsProvider);
    return s3ClientBuilder.build();
  }

  @Bean
  public S3Presigner s3Presigner(Region region, AwsCredentialsProvider awsCredentialsProvider) {
    return S3Presigner.builder().region(region).credentialsProvider(awsCredentialsProvider).build();
  }
}

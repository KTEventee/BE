package com.server.eventee.global.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

  /**
   * S3 업로드, 삭제, 조회용 클라이언트
   */
  @Bean
  @Primary
  public S3Client s3Client(S3Props props) {
    return S3Client.builder()
        .region(Region.of(props.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  /**
   * Presigned URL 발급용 Presigner
   */
  @Bean
  public S3Presigner s3Presigner(S3Props props) {
    return S3Presigner.builder()
        .region(Region.of(props.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  /**
   * 설정값 바인딩용 Properties 클래스
   */
  @Bean
  @ConfigurationProperties(prefix = "aws.s3")
  public S3Props s3Props() {
    return new S3Props();
  }
}

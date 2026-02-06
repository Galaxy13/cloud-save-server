package com.galaxy13.server.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioConfigurationProperties properties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(properties.getEndpoint())
                        .credentials(properties.getAccessKey(), properties.getSecretKey())
                        .build();
        try {
            boolean bucketExists =
                    minioClient.bucketExists(
                            BucketExistsArgs.builder().bucket(properties.getBucket()).build());
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(properties.getBucket()).build());
                log.info("Created MinIO bucket {}", properties.getBucket());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not create MinioClient", e);
        }
        return minioClient;
    }
}

package com.galaxy13.server.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("minio")
@Getter
@NoArgsConstructor
@Setter
public class MinioConfigurationProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucket;
}

package com.conciliaciones.msfilemanagement.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.aws.s3")
public record AwsS3Properties(
        boolean enabled,
        boolean localstackEnabled,
        String region,
        String endpoint,
        String accessKey,
        String secretKey,
        boolean pathStyleAccessEnabled,
        long presignedUrlDurationMinutes,
        String defaultBucket,
        boolean autoCreateDefaultBucket,
        String bucketPrefix
) {
}

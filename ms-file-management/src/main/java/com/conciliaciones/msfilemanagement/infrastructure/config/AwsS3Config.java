package com.conciliaciones.msfilemanagement.infrastructure.config;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@Configuration
@EnableConfigurationProperties(AwsS3Properties.class)
@ConditionalOnProperty(prefix = "app.aws.s3", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AwsS3Config {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(AwsS3Properties properties) {
        if (properties.localstackEnabled()) {
            log.info("LOG INICIO X = awsCredentialsProvider localstack");
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())
            );
        }
        log.info("LOG INICIO X = awsCredentialsProvider aws-real");
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(AwsS3Properties properties, AwsCredentialsProvider credentialsProvider) {
        log.info("LOG INICIO X = s3Client");
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(properties.pathStyleAccessEnabled())
                        .build());

        if (properties.localstackEnabled()) {
            builder.endpointOverride(URI.create(properties.endpoint()));
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsS3Properties properties, AwsCredentialsProvider credentialsProvider) {
        log.info("LOG INICIO X = s3Presigner");
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(properties.pathStyleAccessEnabled())
                        .build());
        if (properties.localstackEnabled()) {
            builder.endpointOverride(URI.create(properties.endpoint()));
        }
        return builder.build();
    }
}

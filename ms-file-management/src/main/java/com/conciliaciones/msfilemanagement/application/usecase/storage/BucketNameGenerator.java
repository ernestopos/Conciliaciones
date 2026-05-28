package com.conciliaciones.msfilemanagement.application.usecase.storage;

import com.conciliaciones.msfilemanagement.infrastructure.config.AwsS3Properties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class BucketNameGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final AwsS3Properties properties;

    public BucketNameGenerator(AwsS3Properties properties) {
        this.properties = properties;
    }

    public String generate() {
        String prefix = sanitize(properties.bucketPrefix());
        String timestamp = LocalDateTime.now().format(FORMATTER).toLowerCase(Locale.ROOT);
        return (prefix + "-" + timestamp).toLowerCase(Locale.ROOT);
    }

    private String sanitize(String value) {
        String input = value == null || value.isBlank() ? "fm" : value;
        return input.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}

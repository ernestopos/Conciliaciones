package com.conciliaciones.msfilemanagement.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.conciliaciones.domain","com.conciliaciones.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = "com.conciliaciones.persistence.repository")
public class JpaModuleConfig {
}

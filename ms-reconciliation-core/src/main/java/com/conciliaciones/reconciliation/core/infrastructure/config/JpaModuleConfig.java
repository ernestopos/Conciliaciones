package com.conciliaciones.reconciliation.core.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.conciliaciones.domain.entity")
@EnableJpaRepositories(basePackages = "com.conciliaciones.persistence.repository")
public class JpaModuleConfig {
}

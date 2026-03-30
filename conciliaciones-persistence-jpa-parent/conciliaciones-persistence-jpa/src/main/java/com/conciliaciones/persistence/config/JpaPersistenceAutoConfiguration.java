package com.conciliaciones.persistence.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EntityScan(basePackages = "com.conciliaciones.persistence.jpa.entity")
@EnableJpaRepositories(basePackages = "com.conciliaciones.persistence.jpa")
@EnableJpaAuditing
@ComponentScan(basePackages = "com.conciliaciones.persistence")
public class JpaPersistenceAutoConfiguration {
}

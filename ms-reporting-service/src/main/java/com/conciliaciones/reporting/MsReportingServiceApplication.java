package com.conciliaciones.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.conciliaciones")
@EntityScan(basePackages = {"com.conciliaciones.domain.entity", "com.conciliaciones.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = "com.conciliaciones.persistence.repository")
public class MsReportingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReportingServiceApplication.class, args);
    }
}

package com.conciliaciones.mssecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.conciliaciones.mssecurity",
        "com.conciliaciones"
})
@EntityScan(basePackages = {
        "com.conciliaciones.domain.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.conciliaciones.persistence.repository"
})
public class MsSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSecurityApplication.class, args);
    }
}

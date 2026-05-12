package com.conciliaciones.mssecurity;

import com.conciliaciones.persistence.repository.AuditLogRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.conciliaciones.mssecurity"
})
@EntityScan(basePackages = {
        "com.conciliaciones.domain.entity"
})
@EnableJpaRepositories(
        basePackages = "com.conciliaciones.persistence.repository",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuditLogRepository.class
        )
)
public class MsSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSecurityApplication.class, args);
    }
}

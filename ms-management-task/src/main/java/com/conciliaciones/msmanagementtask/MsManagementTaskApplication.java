package com.conciliaciones.msmanagementtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.conciliaciones")
public class MsManagementTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsManagementTaskApplication.class, args);
    }
}

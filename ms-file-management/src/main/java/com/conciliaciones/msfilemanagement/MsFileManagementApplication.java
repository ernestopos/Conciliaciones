package com.conciliaciones.msfilemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.conciliaciones")
public class MsFileManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsFileManagementApplication.class, args);
    }
}

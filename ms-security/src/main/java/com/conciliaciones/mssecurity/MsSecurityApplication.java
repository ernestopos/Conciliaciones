package com.conciliaciones.mssecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.conciliaciones.mssecurity"
})
public class MsSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsSecurityApplication.class, args);
    }
}

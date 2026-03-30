package com.conciliaciones.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.conciliaciones.security.infrastructure.config.KeycloakProperties;

//@SpringBootApplication
//@EnableConfigurationProperties(KeycloakProperties.class)
public class MsSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSecurityApplication.class, args);
    }
}

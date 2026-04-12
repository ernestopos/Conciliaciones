package com.conciliaciones.mssecurity.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class BeanConfig {

    @Bean
    public RestClient keycloakRestClient(KeycloakProperties properties) {
        log.info("LOG INICIO X = keycloakRestClient");

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.connectTimeoutMs());
        factory.setReadTimeout(properties.readTimeoutMs());

        RestClient client = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(properties.serverUrl())
                .build();

        log.info("LOG FIN X = keycloakRestClient");
        return client;
    }

    @Bean
    public ObjectMapper objectMapper() {
        log.info("LOG INICIO X = objectMapper");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        log.info("LOG FIN X = objectMapper");
        return mapper;
    }
}
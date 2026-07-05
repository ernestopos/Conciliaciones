package com.conciliaciones.reporting.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reportingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-reporting-service API")
                        .description("API de generación de reportes de Conciliaciones")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Arquitectura Conciliaciones"));
    }
}

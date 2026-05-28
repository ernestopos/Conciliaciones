package com.conciliaciones.reconciliation.core.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reconciliationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-reconciliation-core API")
                        .description("API base del core de conciliación")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Arquitectura Conciliaciones"));
    }
}

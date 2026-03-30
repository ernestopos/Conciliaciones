package com.conciliaciones.orchestrator.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orchestratorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-orchestrator API")
                        .description("API base del microservicio orquestador")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Arquitectura Conciliaciones"));
    }
}

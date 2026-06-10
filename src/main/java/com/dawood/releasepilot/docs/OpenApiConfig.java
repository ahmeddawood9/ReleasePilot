package com.dawood.releasepilot.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI releasePilotOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ReleasePilot Lite API")
                        .version("1.0.0")
                        .description("Deployment tracking API for learning Java and Spring Boot backend development"));
    }
}

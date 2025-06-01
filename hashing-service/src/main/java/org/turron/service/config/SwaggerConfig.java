package org.turron.service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "hashing-service",
                version = "v1",
                description = "Responsible for hashing keyframes logic."
        )
)
public class SwaggerConfig {
}
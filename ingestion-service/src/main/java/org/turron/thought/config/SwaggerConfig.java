package org.turron.thought.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ingestion-service",
                version = "v1",
                description = "Responsible for thought-ingestion logic and deploy to the memory-core."
        )
)
public class SwaggerConfig {
}

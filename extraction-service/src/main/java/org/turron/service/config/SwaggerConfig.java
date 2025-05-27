package org.turron.service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "extraction-service",
                version = "v1",
                description = "Responsible for video keyframes extraction logic."
        )
)
public class SwaggerConfig {
}




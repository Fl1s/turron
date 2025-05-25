package org.turron.upload.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "upload-service",
                version = "v1",
                description = "Responsible for video upload logic."
        )
)
public class SwaggerConfig {
}



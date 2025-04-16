package org.turron.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Swagger
                .route("ingestion_service_docs", r -> r.path("/aggregate/ingestion-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/ingestion-service/v3/api-docs", "/v3/api-docs"))
                        .uri("http://localhost:8091"))

                .route("memory_core_docs", r -> r.path("/aggregate/memory-core/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/memory-core/v3/api-docs", "/v3/api-docs"))
                        .uri("http://localhost:8092"))

                .route("purge_engine_docs", r -> r.path("/aggregate/purge-engine/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/purge-engine/v3/api-docs", "/v3/api-docs"))
                        .uri("http://localhost:8093"))

                // Main
                .route("ingestion_service", r -> r.path("/api/v1/ingest/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("ingestionCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("http://localhost:8091"))

                .route("memory_core", r -> r.path("/api/v1/memory/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("memoryCoreCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("http://localhost:8092"))

                .route("purge_engine", r -> r.path("/api/v1/purge/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("purgeEngineCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("http://localhost:8093"))

                .route("metrics_service", r -> r.path("/api/v1/metrics/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("metricsCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("http://localhost:8094"))

                .route("fallbackRoute", r -> r.path("/fallbackRoute")
                        .filters(f -> f.rewritePath("/fallbackRoute", "/"))
                        .uri("forward:/fallback"))

                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackHandler() {
        return RouterFunctions.route()
                .GET("/fallback", request -> ServerResponse
                        .status(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE)
                        .bodyValue("[ 'Service is unavailable. Please try again later.' ]"))
                .build();
    }
}

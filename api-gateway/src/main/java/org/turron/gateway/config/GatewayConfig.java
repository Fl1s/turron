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

                .route("upload_service_docs", r -> r.path("/aggregate/upload-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/upload-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://upload-service"))

                .route("extraction_service_docs", r -> r.path("/aggregate/extraction-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/extraction-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://extraction-service"))

                .route("hashing_service_docs", r -> r.path("/aggregate/hashing-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/hashing-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://hashing-service"))

                .route("search_service_docs", r -> r.path("/aggregate/search-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/search-service/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://search-service"))


                .route("upload_service", r -> r.path("/api/v1/upload/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("uploadServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://upload-service"))

                .route("extraction_service", r -> r.path("/api/v1/extract/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("extractionServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://extraction-service"))

                .route("hashing_service", r -> r.path("/api/v1/hash/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("hashingServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://hashing-service"))

                .route("search_service", r -> r.path("/api/v1/search/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("searchServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri("lb://search-service"))

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
                        .bodyValue("[ 'Service Unavailable. Please try again later.' ]"))
                .build();
    }
}


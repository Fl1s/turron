package org.turron.thought;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableDiscoveryClient
public class IngestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionServiceApplication.class, args);
    }

}

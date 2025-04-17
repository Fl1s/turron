package org.turron.memory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MemoryCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryCoreApplication.class, args);
    }

}

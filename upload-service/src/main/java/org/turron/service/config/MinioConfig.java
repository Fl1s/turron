package org.turron.service.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.Proxy;
import okhttp3.OkHttpClient;
import java.net.InetSocketAddress;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    @Value("${minio.root-user}")
    private String accessKey;

    @Value("${minio.root-password}")
    private String secretKey;

    @Bean
    @Qualifier("internalMinioClient")
    public MinioClient internalMinioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    @Qualifier("publicMinioClient")
    public MinioClient publicMinioClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("minio", 9000)))
                .build();

        return MinioClient.builder()
                .endpoint(minioPublicUrl)
                .credentials(accessKey, secretKey)
                .httpClient(okHttpClient)
                .build();
    }
}
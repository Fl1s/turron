package org.turron.service.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.root-user}")
    private String accessKey;

    @Value("${minio.root-password}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("minio", 9000)))
//                .build();

        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
//                .httpClient(okHttpClient)
                .build();
    }
}
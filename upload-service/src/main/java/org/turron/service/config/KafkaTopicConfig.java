package org.turron.service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic snippetUploadedTopic() {
        return TopicBuilder.name("snippet.uploaded")
                .build();
    }
    @Bean
    public NewTopic sourceUploadedTopic() {
        return TopicBuilder.name("source.uploaded")
                .build();
    }

    @Bean
    public NewTopic snippetFramesExtractedTopic() {
        return TopicBuilder.name("snippet.frames.extracted")
                .build();
    }
    @Bean
    public NewTopic sourceFramesExtractedTopic() {
        return TopicBuilder.name("source.frames.extracted")
                .build();
    }

    @Bean
    NewTopic snippetFramesHashedTopic() {
        return TopicBuilder.name("snippet.frames.hashed")
                .build();
    }
    @Bean
    NewTopic sourceFramesHashedTopic() {
        return TopicBuilder.name("source.frames.hashed")
                .build();
    }
}

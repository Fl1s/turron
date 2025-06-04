package org.turron.service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic videoUploadedTopic() {
        return TopicBuilder.name("video.uploaded")
                .build();
    }
    @Bean
    public NewTopic sourceUploadedTopic() {
        return TopicBuilder.name("source.uploaded")
                .build();
    }

    @Bean
    public NewTopic videoFramesExtractedTopic() {
        return TopicBuilder.name("video.frames.extracted")
                .build();
    }
    @Bean
    public NewTopic sourceFramesExtractedTopic() {
        return TopicBuilder.name("source.frames.extracted")
                .build();
    }

    @Bean
    NewTopic videoFramesHashedTopic() {
        return TopicBuilder.name("video.frames.hashed")
                .build();
    }
    @Bean
    NewTopic sourceFramesHashedTopic() {
        return TopicBuilder.name("source.frames.hashed")
                .build();
    }
}

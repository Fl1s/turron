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
    public NewTopic frameExtractedTopic() {
        return TopicBuilder.name("video.frames.extracted")
                .build();
    }
    @Bean NewTopic videoExtractedTopic() {
        return TopicBuilder.name("video.frames.hashed")
                .build();
    }
    @Bean NewTopic videoSearchTopic(){
        return TopicBuilder.name("video.match.search")
                .build();
    }
    @Bean NewTopic videoFoundTopic(){
        return TopicBuilder.name("video.match.found")
                .build();
    }
}

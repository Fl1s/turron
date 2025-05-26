package org.turron.upload.config;

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
        return TopicBuilder.name("frame.extracted")
                .build();
    }
}

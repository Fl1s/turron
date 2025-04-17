package org.turron.thought.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic thoughtStatusTopic() {
        return TopicBuilder.name("thought-status")
                .build();
    }

    @Bean
    public NewTopic ingestedThoughtTopic() {
        return TopicBuilder.name("ingested-thought")
                .build();
    }
}

package org.turron.service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.turron.service.event.SourceFrameExtractedEvent;
import org.turron.service.event.SourceFrameHashedEvent;
import org.turron.service.event.VideoFrameExtractedEvent;
import org.turron.service.event.VideoFrameHashedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> producerConfig() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return config;
    }

    @Bean
    public ProducerFactory<String, VideoFrameHashedEvent> VideoFrameHashedEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }


    @Bean
    public KafkaTemplate<String, VideoFrameHashedEvent> VideoFrameHashedEventKafkaTemplate(
            ProducerFactory<String, VideoFrameHashedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, SourceFrameHashedEvent> SourceFrameHashedEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }


    @Bean
    public KafkaTemplate<String, SourceFrameHashedEvent> SourceFrameHashedEventKafkaTemplate(
            ProducerFactory<String, SourceFrameHashedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}



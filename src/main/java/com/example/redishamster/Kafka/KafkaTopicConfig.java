package com.example.redishamster.Kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic topicGet() {
        return new NewTopic("GetHamster", 1, (short) 1);
    }
    @Bean
    public NewTopic topicGetAll() {
        return new NewTopic("GetAllHamsters", 1, (short) 1);
    }
    @Bean
    public NewTopic topicSave() {
        return new NewTopic("SaveHamster", 1, (short) 1);
    }
    @Bean
    public NewTopic update() {
        return new NewTopic("UpdateHamster", 1, (short) 1);
    }
    @Bean
    public NewTopic delete() {
        return new NewTopic("DeleteHamster", 1, (short) 1);
    }
    @Bean
    public NewTopic save() {return new NewTopic("save", 1, (short) 1);}
}

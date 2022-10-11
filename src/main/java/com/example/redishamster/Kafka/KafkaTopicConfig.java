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
    public NewTopic getProductFromDB() {
        return new NewTopic("getProductFromDB", 1, (short) 1);
    }
    @Bean
    public NewTopic sendProductFromDB() {
        return new NewTopic("sendProductFromDB", 1, (short) 1);
    }
    @Bean
    public NewTopic getAllProductsDB() {
        return new NewTopic("getAllProductsDB", 1, (short) 1);
    }

    @Bean
    public NewTopic saveProductDB() {
        return new NewTopic("saveProductDB", 1, (short) 1);
    }

    @Bean
    public NewTopic updateProductDB() {
        return new NewTopic("updateProductDB", 1, (short) 1);
    }

    @Bean
    public NewTopic deleteProductDB() {
        return new NewTopic("deleteProductDB", 1, (short) 1);
    }

    @Bean
    public NewTopic save() {
        return new NewTopic("save", 1, (short) 1);
    }
}

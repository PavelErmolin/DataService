package com.example.redishamster;


import com.example.redishamster.Kafka.MessageListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
public class RedisHamsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisHamsterApplication.class, args);
    }
    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

}

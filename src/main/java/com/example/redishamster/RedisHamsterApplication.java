package com.example.redishamster;


import com.example.redishamster.Kafka.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Slf4j
@SpringBootApplication
@EnableCaching
public class RedisHamsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisHamsterApplication.class, args);
        log.warn("Data service run! " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
    }
    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

}

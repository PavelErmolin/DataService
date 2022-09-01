package com.example.redishamster;

import Model.IHamsterDao;

import com.example.redishamster.Kafka.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedisHamsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisHamsterApplication.class, args);
    }
    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

}

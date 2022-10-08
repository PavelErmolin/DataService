package com.example.redishamster;


import Model.JsonHamsterUser;
import com.example.redishamster.Kafka.MessageListener;
import com.example.redishamster.Kafka.MessageProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.management.ObjectName;
import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableCaching
public class RedisHamsterApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RedisHamsterApplication.class, args);
        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);

    }
    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

}

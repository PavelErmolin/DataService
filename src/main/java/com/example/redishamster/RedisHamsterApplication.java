package com.example.redishamster;

import Model.IHamsterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisHamsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisHamsterApplication.class, args);
    }

}

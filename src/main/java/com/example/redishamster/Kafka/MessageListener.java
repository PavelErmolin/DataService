package com.example.redishamster.Kafka;


import com.example.redishamster.Model.Hamster;
import com.example.redishamster.Repository.HamsterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class MessageListener {

    @Autowired
    private HamsterService hamsterService;

    @KafkaListener(topics = "${kafka.topic.name}", containerFactory = "kafkaListenerContainerFactory")
    public void listener(Hamster ham) {
        System.out.println("Recieved message: " + ham);
        hamsterService.saveHamster(ham);
    }
}

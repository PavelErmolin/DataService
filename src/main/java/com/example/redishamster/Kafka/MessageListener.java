package com.example.redishamster.Kafka;

import DTO.Data;
import Model.Hamster;
import Model.IHamsterDao;
import com.example.consumer.Repository.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class MessageListener {

    @Autowired
    private IHamsterDao hamDao;

    @KafkaListener(topics = "${kafka.topic.name}", containerFactory = "kafkaListenerContainerFactory")
    public void listener(Hamster ham) {
        System.out.println("Recieved message: " + ham);
        hamDao.saveHamster(ham);
    }
}

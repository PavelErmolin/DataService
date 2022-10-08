package com.example.redishamster.Kafka;

import Model.JsonHamsterUser;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@NoArgsConstructor
@Component
public class UserProducer {

    @Autowired
    private KafkaTemplate<String, JsonHamsterUser> userKafkaTemplate;


    public void sendMessage(String topicName, JsonHamsterUser user) {
        ListenableFuture<SendResult<String, JsonHamsterUser>> future = userKafkaTemplate.send(topicName, user);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send message = {} dut to: {}", user, throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, JsonHamsterUser> stringDataSendResult) {
                log.info("Sent Message = {} with offset = {}", user, stringDataSendResult.getRecordMetadata().offset());
            }
        });
    }
}
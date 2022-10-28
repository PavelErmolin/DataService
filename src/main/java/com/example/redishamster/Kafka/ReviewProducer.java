package com.example.redishamster.Kafka;


import com.example.orchestrator.model.JsonReview;
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
public class ReviewProducer {

    @Autowired
    private KafkaTemplate<String, JsonReview> kafkaReviewTemplate;


    public void sendMessage(String topicName, JsonReview review) {
        ListenableFuture<SendResult<String, JsonReview>> future = kafkaReviewTemplate.send(topicName, review);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send message = {} dut to: {}", review, throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, JsonReview> stringDataSendResult) {
                log.info("Sent Message = {} with offset = {}", review, stringDataSendResult.getRecordMetadata().offset());
            }
        });
    }

}


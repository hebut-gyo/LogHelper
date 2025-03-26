package com.gyo.loghelper.config;

import com.gyo.loghelper.entity.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    private final MongoTemplate mongoTemplate;
    public Consumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @RabbitListener(queues = "log-helper")
    public void receive(Log message) {
        mongoTemplate.save(message,"logs");
    }
}
package com.siti.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @RabbitListener(queues = "consumerQueue")
    public void receiveMessage(String message) {

        System.out.println("Received: " + message);
    }
}
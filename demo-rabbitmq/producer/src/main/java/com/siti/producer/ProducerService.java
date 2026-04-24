package com.siti.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public void sendMessage(Map<String, Object> order) {

        try {

            String json =
                    objectMapper.writeValueAsString(order);

            rabbitTemplate.convertAndSend(
                    "consumerQueue",
                    json
            );

            rabbitTemplate.convertAndSend(
                    "emailQueue",
                    json
            );

            System.out.println("Sent: " + json);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
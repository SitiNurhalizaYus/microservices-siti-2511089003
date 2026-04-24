package com.siti.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue consumerQueue() {
        return new Queue("consumerQueue", false);
    }
}
package com.siti.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nama queue ini HARUS sama dengan yang dipakai EmailConsumer di email-service
    public static final String EMAIL_QUEUE = "emailQueue";

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true); // durable=true, biar pesan tidak hilang saat RabbitMQ restart
    }

    // Pakai JSON converter, bukan default Java serialization,
    // supaya pesan yang dikirim bisa dibaca sebagai String JSON oleh Email Service.
    // PENTING: pakai ObjectMapper bean dari OrderApplication (objectMapper()),
    // bukan "new ObjectMapper()" polos -- karena Order punya field LocalDateTime
    // (createdAt) yang butuh JavaTimeModule supaya bisa diserialize ke JSON.
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(@Qualifier("objectMapper") ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}

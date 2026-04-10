package com.siti.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.order.model.Order;
import com.siti.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.siti.order.config.RabbitMQConfig.EMAIL_QUEUE;

@Service
public class OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Order createOrder(Order order) {
        // 1. Simpan order ke MySQL (orderdb) lebih dulu
        Order savedOrder = orderRepository.save(order);

        // 2. Kirim notifikasi order ke Email Service lewat RabbitMQ
        try {
            String json = objectMapper.writeValueAsString(savedOrder);
            System.out.println("=== ORDER JSON DIKIRIM KE QUEUE ===");
            System.out.println(json);
            System.out.println("===================================");
        } catch (Exception e) {
            System.err.println("Error serializing order: " + e.getMessage());
        }
        rabbitTemplate.convertAndSend(EMAIL_QUEUE, savedOrder);

        return savedOrder;
    }
}

package com.siti.email_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class EmailConsumer {

    @Autowired
    private EmailSenderService emailSenderService;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @RabbitListener(queues = "emailQueue")
    public void receiveOrder(String jsonStr) {

        try {

            System.out.println("=== PESAN DITERIMA ===");
            System.out.println(jsonStr);

            Map<String, Object> order =
                    objectMapper.readValue(
                            jsonStr,
                            Map.class
                    );

            String email =
                    (String) order.get("email");

            String productName =
                    (String) order.get("productName");

            int quantity =
                    Integer.parseInt(
                            order.get("quantity").toString()
                    );

            BigDecimal total =
                    new BigDecimal(
                            order.get("totalPrice").toString()
                    );

            emailSenderService.sendEmail(
                    email,
                    productName,
                    quantity,
                    total
            );

            System.out.println(
                    "Email berhasil dikirim ke: "
                            + email
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
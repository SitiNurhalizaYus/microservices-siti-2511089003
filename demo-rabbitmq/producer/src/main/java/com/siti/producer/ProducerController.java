package com.siti.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProducerController {

    @Autowired
    private ProducerService producerService;

    @PostMapping("/send")
    public String sendMessage(
            @RequestBody Map<String, Object> order
    ) {

        producerService.sendMessage(order);

        return "Order sent successfully!";
    }
}
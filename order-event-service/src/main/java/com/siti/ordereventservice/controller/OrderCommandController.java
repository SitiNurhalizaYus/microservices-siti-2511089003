package com.siti.ordereventservice.controller;

import com.siti.ordereventservice.command.ChangeOrderStatusCommand;
import com.siti.ordereventservice.command.CreateOrderCommand;
import com.siti.ordereventservice.command.OrderCommandService;
import com.siti.ordereventservice.model.OrderEvent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * COMMAND CONTROLLER (CQRS - sisi tulis).
 * Semua endpoint di sini cuma MENAMBAH event baru, tidak pernah mengembalikan
 * "status order saat ini" secara langsung - untuk itu pakai
 * OrderQueryController. Pemisahan ini sengaja dibuat eksplisit di level
 * routing supaya konsep CQRS terlihat jelas: /commands/** untuk menulis,
 * /queries/** untuk membaca.
 */
@RestController
@RequestMapping("/commands/orders")
public class OrderCommandController {

    @Autowired
    private OrderCommandService orderCommandService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        try {
            OrderEvent event = orderCommandService.handleCreateOrder(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal mencatat event: " + e.getMessage());
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<?> payOrder(@Valid @RequestBody ChangeOrderStatusCommand command) {
        try {
            OrderEvent event = orderCommandService.handlePayOrder(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal mencatat event: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@Valid @RequestBody ChangeOrderStatusCommand command) {
        try {
            OrderEvent event = orderCommandService.handleCancelOrder(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal mencatat event: " + e.getMessage());
        }
    }

    @PostMapping("/ship")
    public ResponseEntity<?> shipOrder(@Valid @RequestBody ChangeOrderStatusCommand command) {
        try {
            OrderEvent event = orderCommandService.handleShipOrder(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal mencatat event: " + e.getMessage());
        }
    }
}

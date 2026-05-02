package com.siti.ordereventservice.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.ordereventservice.model.OrderEvent;
import com.siti.ordereventservice.repository.OrderEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * COMMAND SIDE (CQRS) - satu-satunya tanggung jawab class ini adalah
 * MENULIS event baru ke event_store. Class ini TIDAK PERNAH membaca atau
 * menghitung status order saat ini - itu tanggung jawab OrderProjectionService
 * (Query side). Pemisahan ini adalah inti dari CQRS: jalur tulis dan jalur
 * baca benar-benar terpisah, masing-masing dengan tanggung jawabnya sendiri.
 */
@Service
public class OrderCommandService {

    @Autowired
    private OrderEventRepository orderEventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderEvent handleCreateOrder(CreateOrderCommand command) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("productName", command.getProductName());
        payload.put("quantity", command.getQuantity());
        payload.put("totalPrice", command.getTotalPrice());

        OrderEvent event = new OrderEvent();
        event.setOrderId(command.getOrderId());
        event.setEventType("ORDER_CREATED");
        event.setPayload(objectMapper.writeValueAsString(payload));

        return orderEventRepository.save(event);
    }

    public OrderEvent handlePayOrder(ChangeOrderStatusCommand command) throws Exception {
        return appendStatusEvent(command, "ORDER_PAID");
    }

    public OrderEvent handleCancelOrder(ChangeOrderStatusCommand command) throws Exception {
        return appendStatusEvent(command, "ORDER_CANCELLED");
    }

    public OrderEvent handleShipOrder(ChangeOrderStatusCommand command) throws Exception {
        return appendStatusEvent(command, "ORDER_SHIPPED");
    }

    private OrderEvent appendStatusEvent(ChangeOrderStatusCommand command, String eventType) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        if (command.getNote() != null) {
            payload.put("note", command.getNote());
        }

        OrderEvent event = new OrderEvent();
        event.setOrderId(command.getOrderId());
        event.setEventType(eventType);
        event.setPayload(objectMapper.writeValueAsString(payload));

        // PENTING: ini cuma INSERT baris baru, tidak pernah UPDATE baris
        // event yang sudah ada sebelumnya - sifat append-only inilah yang
        // membuat seluruh riwayat order tetap utuh dan bisa "diputar ulang"
        // dari awal kapan saja.
        return orderEventRepository.save(event);
    }
}

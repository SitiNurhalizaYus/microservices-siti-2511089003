package com.siti.ordereventservice.controller;

import com.siti.ordereventservice.model.OrderEvent;
import com.siti.ordereventservice.model.OrderProjection;
import com.siti.ordereventservice.query.OrderProjectionService;
import com.siti.ordereventservice.repository.OrderEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QUERY CONTROLLER (CQRS - sisi baca).
 * Semua endpoint di sini cuma MEMBACA dan menghitung ulang status order dari
 * event yang sudah tersimpan - tidak pernah menulis/mengubah apapun.
 */
@RestController
@RequestMapping("/queries/orders")
public class OrderQueryController {

    @Autowired
    private OrderProjectionService orderProjectionService;

    @Autowired
    private OrderEventRepository orderEventRepository;

    // Status order saat ini, dihitung dari replay semua event miliknya
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable String orderId) {
        try {
            return orderProjectionService.getProjection(orderId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Gagal menghitung status order: " + e.getMessage());
        }
    }

    // Daftar semua order beserta status terkininya (masing-masing dihitung ulang dari event)
    @GetMapping
    public ResponseEntity<List<OrderProjection>> getAllOrders() {
        try {
            return ResponseEntity.ok(orderProjectionService.getAllProjections());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Riwayat event MENTAH (raw) milik satu order - untuk transparansi penuh,
    // menunjukkan persis apa yang tersimpan di event_store sebelum di-replay
    @GetMapping("/{orderId}/raw-events")
    public ResponseEntity<List<OrderEvent>> getRawEvents(@PathVariable String orderId) {
        List<OrderEvent> events = orderEventRepository.findByOrderIdOrderByOccurredAtAsc(orderId);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }
}

package com.siti.ordereventservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Ini jantung dari Event Sourcing: setiap baris di tabel ini adalah SATU
 * kejadian yang pernah terjadi pada sebuah order, dan baris ini TIDAK PERNAH
 * diubah atau dihapus setelah ditulis (append-only).
 *
 * Status order yang "sekarang" tidak disimpan langsung sebagai kolom -
 * melainkan DIHITUNG ULANG dari membaca seluruh riwayat event milik orderId
 * tertentu, urut dari yang paling lama ke paling baru. Lihat
 * OrderProjectionService untuk logic penghitungannya.
 */
@Entity
@Table(name = "event_store")
public class OrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    // Jenis event: ORDER_CREATED, ORDER_PAID, ORDER_CANCELLED, ORDER_SHIPPED
    @Column(nullable = false)
    private String eventType;

    // Data tambahan terkait event ini, disimpan sebagai JSON string
    // (misal saat ORDER_CREATED: {"productName":"Laptop","quantity":2,"totalPrice":30000000})
    @Column(length = 2000)
    private String payload;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    protected void onCreate() {
        occurredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}

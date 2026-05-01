package com.siti.ordereventservice.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ini BUKAN entity database - ini representasi "status order saat ini"
 * yang dihasilkan dengan menghitung ulang (replay) seluruh OrderEvent
 * milik orderId tertentu. Inilah sisi QUERY dari CQRS: model baca yang
 * terpisah dari model tulis (OrderEvent).
 */
public class OrderProjection {

    private String orderId;
    private String currentStatus;       // hasil hitung ulang dari event terakhir yang relevan
    private String productName;
    private Integer quantity;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<String> eventHistory;  // riwayat singkat semua event yang terjadi, untuk transparansi

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }

    public List<String> getEventHistory() { return eventHistory; }
    public void setEventHistory(List<String> eventHistory) { this.eventHistory = eventHistory; }
}

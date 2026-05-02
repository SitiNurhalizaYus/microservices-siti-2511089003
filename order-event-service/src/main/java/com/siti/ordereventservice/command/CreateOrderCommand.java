package com.siti.ordereventservice.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Command = "perintah untuk mengubah sesuatu". Setiap command yang diterima
 * akan menghasilkan SATU OrderEvent baru yang ditambahkan ke event_store -
 * tidak pernah langsung mengubah baris yang sudah ada.
 */
public class CreateOrderCommand {

    @NotBlank(message = "orderId tidak boleh kosong")
    private String orderId;

    @NotBlank(message = "productName tidak boleh kosong")
    private String productName;

    @NotNull
    @Positive(message = "quantity harus lebih dari 0")
    private Integer quantity;

    @NotNull
    @Positive(message = "totalPrice harus lebih dari 0")
    private Double totalPrice;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}

package com.siti.ordereventservice.command;

import jakarta.validation.constraints.NotBlank;

/**
 * Command sederhana untuk mengubah status order (bayar, batal, kirim).
 * Setiap pemanggilan akan menambah SATU event baru, bukan mengubah event lama.
 */
public class ChangeOrderStatusCommand {

    @NotBlank(message = "orderId tidak boleh kosong")
    private String orderId;

    private String note; // catatan opsional, misal alasan pembatalan

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

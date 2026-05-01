package com.siti.ordereventservice.repository;

import com.siti.ordereventservice.model.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

    // Ambil semua event milik satu orderId, urut dari paling lama -> paling baru.
    // Urutan ini PENTING karena status akhir dihitung dengan "memutar ulang"
    // event satu per satu sesuai urutan kejadiannya.
    List<OrderEvent> findByOrderIdOrderByOccurredAtAsc(String orderId);

    // Ambil semua orderId unik yang pernah punya event (untuk endpoint list semua order)
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT e.orderId FROM OrderEvent e")
    List<String> findAllDistinctOrderIds();
}

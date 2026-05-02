package com.siti.ordereventservice.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siti.ordereventservice.model.OrderEvent;
import com.siti.ordereventservice.model.OrderProjection;
import com.siti.ordereventservice.repository.OrderEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * QUERY SIDE (CQRS) - satu-satunya tanggung jawab class ini adalah MEMBACA
 * event_store dan menghitung ulang ("replay") status order saat ini.
 * Class ini TIDAK PERNAH menulis apapun ke database - itu tanggung jawab
 * OrderCommandService (Command side).
 *
 * Ini adalah inti dari Event Sourcing: status "sekarang" bukan disimpan
 * sebagai kolom yang langsung di-update, melainkan SELALU dihitung ulang
 * dari awal setiap kali dibutuhkan, dengan memproses event satu per satu
 * sesuai urutan kejadiannya (paling lama -> paling baru).
 */
@Service
public class OrderProjectionService {

    @Autowired
    private OrderEventRepository orderEventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<OrderProjection> getProjection(String orderId) throws Exception {
        List<OrderEvent> events = orderEventRepository.findByOrderIdOrderByOccurredAtAsc(orderId);

        if (events.isEmpty()) {
            return Optional.empty();
        }

        OrderProjection projection = new OrderProjection();
        projection.setOrderId(orderId);
        List<String> history = new ArrayList<>();

        // INI BAGIAN PALING PENTING: replay setiap event satu per satu,
        // urut dari yang paling lama, dan terapkan efeknya ke projection.
        // Status akhir adalah HASIL AKUMULASI dari semua event ini,
        // bukan nilai yang disimpan langsung di satu tempat.
        for (OrderEvent event : events) {
            applyEvent(projection, event);
            history.add(event.getEventType() + " @ " + event.getOccurredAt());
        }

        projection.setEventHistory(history);
        return Optional.of(projection);
    }

    public List<OrderProjection> getAllProjections() throws Exception {
        List<String> allOrderIds = orderEventRepository.findAllDistinctOrderIds();
        List<OrderProjection> result = new ArrayList<>();
        for (String orderId : allOrderIds) {
            getProjection(orderId).ifPresent(result::add);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void applyEvent(OrderProjection projection, OrderEvent event) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(event.getPayload(), Map.class);

        switch (event.getEventType()) {
            case "ORDER_CREATED":
                projection.setProductName((String) payload.get("productName"));
                projection.setQuantity((Integer) payload.get("quantity"));
                projection.setTotalPrice(((Number) payload.get("totalPrice")).doubleValue());
                projection.setCurrentStatus("CREATED");
                projection.setCreatedAt(event.getOccurredAt());
                break;
            case "ORDER_PAID":
                projection.setCurrentStatus("PAID");
                break;
            case "ORDER_SHIPPED":
                projection.setCurrentStatus("SHIPPED");
                break;
            case "ORDER_CANCELLED":
                projection.setCurrentStatus("CANCELLED");
                break;
            default:
                // event type tidak dikenal, diabaikan saja (forward-compatible
                // kalau nanti ada jenis event baru yang ditambahkan)
                break;
        }
        projection.setLastUpdatedAt(event.getOccurredAt());
    }
}

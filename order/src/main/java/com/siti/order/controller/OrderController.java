package com.siti.order.controller;

import com.siti.order.model.Order;
import com.siti.order.model.OrderRequest;
import com.siti.order.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            String json = restTemplate.getForObject(
                "http://product-service/products", String.class
            );
            List<Map<String, Object>> products = objectMapper.readValue(
                json, new TypeReference<List<Map<String, Object>>>() {}
            );
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            String json = restTemplate.getForObject(
                "http://product-service/products/" + id, String.class
            );
            Map<String, Object> product = objectMapper.readValue(
                json, new TypeReference<Map<String, Object>>() {}
            );
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try {
            String json = restTemplate.getForObject(
                "http://product-service/products/" + request.getProductId(), String.class
            );
            Map<String, Object> product = objectMapper.readValue(
                json, new TypeReference<Map<String, Object>>() {}
            );

            if (product == null) {
                return ResponseEntity.badRequest().body("Produk tidak ditemukan");
            }

            BigDecimal price = new BigDecimal(product.get("price").toString());
            BigDecimal quantity = new BigDecimal(request.getQuantity());
            BigDecimal total = price.multiply(quantity);

            String productName = product.get("name").toString();

            Order order = new Order();
            order.setProductId(request.getProductId());
            order.setProductName(productName);
            order.setQuantity(request.getQuantity());
            order.setTotalPrice(total.doubleValue());
            order.setEmail(request.getEmail());

            Order savedOrder = orderService.createOrder(order);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", savedOrder.getId());
            response.put("product", product);
            response.put("quantity", request.getQuantity());
            response.put("totalPrice", total);
            response.put("email", request.getEmail());
            response.put("status", "CREATED");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

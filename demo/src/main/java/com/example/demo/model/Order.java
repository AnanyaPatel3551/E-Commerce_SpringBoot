package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
public class Order {
    @Id
    private String id;

    private String userId;
    private List<CartItem> items; // We snapshot the items at the time of purchase
    private Double totalAmount;
    private String status; // e.g., "PENDING", "PAID", "SHIPPED"
    private LocalDateTime createdAt;
}
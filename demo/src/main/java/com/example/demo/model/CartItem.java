package com.example.demo.model;

import lombok.Data;

@Data
public class CartItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price; // Price at the time of adding
}
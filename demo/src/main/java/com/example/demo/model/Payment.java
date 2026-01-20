package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
public class Payment {
    @Id
    private String id;

    private String orderId; // Your internal Order ID
    private String userId;

    // Razorpay Fields
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private Double amount;
    private String status; // "CREATED", "SUCCESS", "FAILED"
    private LocalDateTime paymentDate;
}
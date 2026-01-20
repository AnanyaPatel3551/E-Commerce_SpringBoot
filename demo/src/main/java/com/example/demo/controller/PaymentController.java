package com.example.demo.controller;

import com.example.demo.model.Payment;
import com.example.demo.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Step 1: Create Razorpay Order
    @PostMapping("/create/{orderId}")
    public Payment createPaymentOrder(@PathVariable String orderId) throws RazorpayException {
        return paymentService.createRazorpayOrder(orderId);
    }

    // Step 2: Verify Payment (Usually called by Frontend after success)
    @PostMapping("/verify")
    public Payment verifyPayment(@RequestBody Map<String, String> data) {
        return paymentService.verifyPayment(
                data.get("orderId"),
                data.get("razorpayPaymentId"),
                data.get("razorpayOrderId"),
                data.get("razorpaySignature")
        );
    }
}
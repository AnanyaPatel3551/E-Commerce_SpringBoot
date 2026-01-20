package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.Payment;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // 1. Initiate Transaction (Ask Razorpay for an Order ID)
    public Payment createRazorpayOrder(String orderId) throws RazorpayException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Initialize Razorpay Client
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        // Prepare Request
        JSONObject options = new JSONObject();
        // Razorpay expects amount in PAISE (100 INR = 10000 paise)
        options.put("amount", (int)(order.getTotalAmount() * 100));
        options.put("currency", "INR");
        options.put("receipt", "txn_" + orderId);

        // Call Razorpay API
        com.razorpay.Order razorpayOrder = client.orders.create(options);

        // Save initial payment record
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getTotalAmount());
        payment.setRazorpayOrderId(razorpayOrder.get("id")); // "order_M7..."
        payment.setStatus("CREATED");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // 2. Verify Payment (Called after user pays on frontend)
    public Payment verifyPayment(String orderId, String rzpPaymentId, String rzpOrderId, String rzpSignature) {

        // 1. Verify Signature manually
        String generatedSignature = null;
        try {
            String data = rzpOrderId + "|" + rzpPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(), "HmacSHA256"));
            byte[] hex = mac.doFinal(data.getBytes());
            // Java 17+ native way to convert bytes to Hex
            generatedSignature = java.util.HexFormat.of().formatHex(hex);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying signature");
        }

        if (!generatedSignature.equals(rzpSignature)) {
            throw new RuntimeException("Payment signature verification failed!");
        }

        // 2. Update Payment Status
        Payment payment = paymentRepository.findAll().stream()
                .filter(p -> p.getRazorpayOrderId().equals(rzpOrderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        payment.setRazorpayPaymentId(rzpPaymentId);
        payment.setRazorpaySignature(rzpSignature);
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        // 3. Update Order Status
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("PAID");
        orderRepository.save(order);

        return payment;
    }
}
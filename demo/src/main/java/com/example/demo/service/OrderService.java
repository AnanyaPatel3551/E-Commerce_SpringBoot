package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public Order placeOrder(String userId) {
        // 1. Find the User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if cart is empty
        if (user.getCart().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Calculate Total Price
        double total = 0.0;
        for (CartItem item : user.getCart()) {
            total += item.getPrice() * item.getQuantity();
        }

        // 4. Create the Order
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(List.copyOf(user.getCart())); // Copy items so they don't change later
        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING"); // Default status

        Order savedOrder = orderRepository.save(order);

        // 5. CLEAR the User's Cart and Save
        user.getCart().clear();
        userRepository.save(user);

        return savedOrder;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Create User
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Get User (View Cart)
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // Add to Cart
    // URL: /api/users/{userId}/cart?productId={productId}&quantity=1
    @PostMapping("/{userId}/cart")
    public User addToCart(
            @PathVariable String userId,
            @RequestParam String productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        return userService.addToCart(userId, productId, quantity);
    }

    // Remove from Cart
    @DeleteMapping("/{userId}/cart/{productId}")
    public User removeFromCart(@PathVariable String userId, @PathVariable String productId) {
        return userService.removeFromCart(userId, productId);
    }
}
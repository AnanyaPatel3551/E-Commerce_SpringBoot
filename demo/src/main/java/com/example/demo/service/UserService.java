package com.example.demo.service;

import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. Create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 2. Get user details (including their cart)
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 3. Add a product to the user's cart
    public User addToCart(String userId, String productId, int quantity) {
        // Find the User
        User user = getUserById(userId);

        // Find the Product (to get name and price)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create the Cart Item
        CartItem item = new CartItem();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);

        // Add to User's cart list
        user.getCart().add(item);

        // Save the updated User back to MongoDB
        return userRepository.save(user);
    }

    // 4. Remove item from cart (Optional but useful)
    public User removeFromCart(String userId, String productId) {
        User user = getUserById(userId);
        user.getCart().removeIf(item -> item.getProductId().equals(productId));
        return userRepository.save(user);
    }
}
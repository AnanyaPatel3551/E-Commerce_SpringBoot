package com.example.demo.repository;


import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Helper method to find a user by email (useful for login later)
    Optional<User> findByEmail(String email);
}
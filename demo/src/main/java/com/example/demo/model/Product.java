package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "products")
@Data
public class Product {

    @Id
    private String id; // MongoDB uses String IDs (ObjectIds)

    private String name;
    private Double price;
    private String description;
    private String category;
}
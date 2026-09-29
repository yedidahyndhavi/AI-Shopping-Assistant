package com.aishopping.shoppingassistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String brand;

    private double price;

    private String category;

    private double rating;

    private String description;

    private boolean available;

    // Default constructor
    public Product() {
    }

    // Existing constructor
    public Product(String name,
                   String brand,
                   double price,
                   String category,
                   double rating,
                   String description) {

        this.name = name;
        this.brand = brand;
        this.price = price;
        this.category = category;
        this.rating = rating;
        this.description = description;
        this.available = true;
    }

    // Get ID
    public Long getId() {
        return id;
    }

    // Set ID
    public void setId(Long id) {
        this.id = id;
    }

    // Get name
    public String getName() {
        return name;
    }

    // Set name
    public void setName(String name) {
        this.name = name;
    }

    // Get brand
    public String getBrand() {
        return brand;
    }

    // Set brand
    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Get price
    public double getPrice() {
        return price;
    }

    // Set price
    public void setPrice(double price) {
        this.price = price;
    }

    // Get category
    public String getCategory() {
        return category;
    }

    // Set category
    public void setCategory(String category) {
        this.category = category;
    }

    // Get rating
    public double getRating() {
        return rating;
    }

    // Set rating
    public void setRating(double rating) {
        this.rating = rating;
    }

    // Get description
    public String getDescription() {
        return description;
    }

    // Set description
    public void setDescription(String description) {
        this.description = description;
    }

    // Get availability
    public boolean isAvailable() {
        return available;
    }

    // Set availability
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
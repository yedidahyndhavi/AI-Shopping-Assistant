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

    public Product() {
    }

    public Product(String name, String brand, double price,
               String category, double rating, String description) {
    this.name = name;
    this.brand = brand;
    this.price = price;
    this.category = category;
    this.rating = rating;
    this.description = description;
}
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String getCategory() {
    return category;
}

public void setCategory(String category) {
    this.category = category;
}

public double getRating() {
    return rating;
}

public void setRating(double rating) {
    this.rating = rating;
}

public String getDescription() {
    return description;
}

public void setDescription(String description) {
    this.description = description;
}
public boolean isAvailable() {
    return available;
}

public void setAvailable(boolean available) {
    this.available = available;
}
}

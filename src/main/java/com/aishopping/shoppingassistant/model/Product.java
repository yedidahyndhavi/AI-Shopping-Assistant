package com.aishopping.shoppingassistant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductSpecification> specifications =
            new ArrayList<>();

    public Product() {
    }

    public Product(
            String name,
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

    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
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

    public List<ProductSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(
            List<ProductSpecification> specifications) {

        this.specifications = specifications;
    }

    // =========================
    // SPECIFICATION METHODS
    // =========================

    public void addSpecification(
            ProductSpecification specification) {

        specifications.add(specification);
        specification.setProduct(this);
    }

    public void removeSpecification(
            ProductSpecification specification) {

        specifications.remove(specification);
        specification.setProduct(null);
    }
}
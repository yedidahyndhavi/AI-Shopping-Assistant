package com.aishopping.shoppingassistant.repository;

import com.aishopping.shoppingassistant.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByPriceLessThanEqual(double maxPrice);

    List<Product> findByRatingGreaterThanEqual(double minRating);

    List<Product> findByAvailableTrue();
}
package com.aishopping.shoppingassistant.repository;

import com.aishopping.shoppingassistant.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
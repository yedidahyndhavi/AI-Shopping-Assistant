package com.aishopping.shoppingassistant.repository;

import com.aishopping.shoppingassistant.model.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSpecificationRepository
        extends JpaRepository<ProductSpecification, Long> {

    List<ProductSpecification> findByProductId(Long productId);

    List<ProductSpecification> findBySpecificationNameIgnoreCase(
            String specificationName);
}
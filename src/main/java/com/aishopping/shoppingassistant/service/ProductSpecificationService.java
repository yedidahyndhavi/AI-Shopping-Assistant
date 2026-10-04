package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.repository.ProductRepository;
import com.aishopping.shoppingassistant.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductSpecificationService {

    private final ProductSpecificationRepository
            productSpecificationRepository;

    private final ProductRepository productRepository;

    public ProductSpecificationService(
            ProductSpecificationRepository productSpecificationRepository,
            ProductRepository productRepository) {

        this.productSpecificationRepository =
                productSpecificationRepository;

        this.productRepository = productRepository;
    }

    public ProductSpecification addSpecification(
            Long productId,
            ProductSpecification specification) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        specification.setProduct(product);

        return productSpecificationRepository.save(
                specification);
    }

    public List<ProductSpecification> getSpecifications(
            Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new RuntimeException(
                    "Product not found");
        }

        return productSpecificationRepository
                .findByProductId(productId);
    }

    public ProductSpecification getSpecification(
            Long specificationId) {

        return productSpecificationRepository
                .findById(specificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Specification not found"));
    }

    public ProductSpecification updateSpecification(
            Long specificationId,
            ProductSpecification updatedSpecification) {

        ProductSpecification existingSpecification =
                getSpecification(specificationId);

        existingSpecification.setSpecificationName(
                updatedSpecification.getSpecificationName());

        existingSpecification.setSpecificationValue(
                updatedSpecification.getSpecificationValue());

        existingSpecification.setUnit(
                updatedSpecification.getUnit());

        existingSpecification.setNumericValue(
                updatedSpecification.getNumericValue());

        return productSpecificationRepository.save(
                existingSpecification);
    }

    public void deleteSpecification(
            Long specificationId) {

        ProductSpecification specification =
                getSpecification(specificationId);

        productSpecificationRepository.delete(
                specification);
    }
    public void addOrUpdateSpecifications(
        Long productId,
        List<ProductSpecification> specifications) {

    Product product =
            productRepository.findById(productId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Product not found"));

    for (ProductSpecification incoming : specifications) {

        ProductSpecification existing =
                productSpecificationRepository
                        .findByProductId(productId)
                        .stream()
                        .filter(spec ->
                                spec.getSpecificationName()
                                        .equalsIgnoreCase(
                                                incoming.getSpecificationName()))
                        .findFirst()
                        .orElse(null);

        if (existing != null) {

            existing.setSpecificationValue(
                    incoming.getSpecificationValue());

            existing.setUnit(
                    incoming.getUnit());

            existing.setNumericValue(
                    incoming.getNumericValue());

            productSpecificationRepository.save(existing);

        } else {

            incoming.setProduct(product);

            productSpecificationRepository.save(
                    incoming);
        }
    }
}
}
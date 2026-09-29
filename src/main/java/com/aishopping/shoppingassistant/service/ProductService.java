package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;
import com.aishopping.shoppingassistant.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEvaluationService productEvaluationService;

    public ProductService(
            ProductRepository productRepository,
            ProductEvaluationService productEvaluationService) {

        this.productRepository = productRepository;
        this.productEvaluationService = productEvaluationService;
    }

    // Get all products
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // Get only available products
    public List<Product> getAvailableProducts() {

        return productRepository.findByAvailableTrue();
    }

    // Get product by ID
    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    // Add a new product
    public Product addProduct(Product product) {

        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(
            Long id,
            Product updatedProduct) {

        Product existingProduct =
                getProductById(id);

        existingProduct.setName(
                updatedProduct.getName());

        existingProduct.setBrand(
                updatedProduct.getBrand());

        existingProduct.setPrice(
                updatedProduct.getPrice());

        existingProduct.setCategory(
                updatedProduct.getCategory());

        existingProduct.setRating(
                updatedProduct.getRating());

        existingProduct.setDescription(
                updatedProduct.getDescription());

        existingProduct.setAvailable(
                updatedProduct.isAvailable());

        return productRepository.save(
                existingProduct);
    }

    // Search products by name
    public List<Product> searchProducts(
            String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name);
    }

    // Filter products by category
    public List<Product> filterByCategory(
            String category) {

        return productRepository
                .findByCategoryIgnoreCase(category);
    }

    // Filter products by maximum price
    public List<Product> filterByMaxPrice(
            double maxPrice) {

        return productRepository
                .findByPriceLessThanEqual(maxPrice);
    }

    // Filter products by minimum rating
    public List<Product> filterByMinRating(
            double minRating) {

        return productRepository
                .findByRatingGreaterThanEqual(minRating);
    }

    // Basic product comparison
    public ProductComparison compareProducts(
            List<Long> ids) {

        if (ids == null || ids.size() != 2) {

            throw new IllegalArgumentException(
                    "Exactly two product IDs are required");
        }

        Product product1 =
                getProductById(ids.get(0));

        Product product2 =
                getProductById(ids.get(1));

        double priceDifference =
                Math.abs(
                        product1.getPrice()
                                - product2.getPrice());

        String cheaperProduct;

        if (product1.getPrice()
                < product2.getPrice()) {

            cheaperProduct =
                    product1.getName();

        } else if (product2.getPrice()
                < product1.getPrice()) {

            cheaperProduct =
                    product2.getName();

        } else {

            cheaperProduct = "Both products have the same price";
        }

        return new ProductComparison(
                product1,
                product2,
                priceDifference,
                cheaperProduct);
    }

    // Detailed product comparison
    public ProductComparisonResponse compareProductsDetailed(
            List<Long> ids) {

        if (ids == null || ids.size() != 2) {

            throw new IllegalArgumentException(
                    "Exactly two product IDs are required");
        }

        Product product1 =
                getProductById(ids.get(0));

        Product product2 =
                getProductById(ids.get(1));

        // Price comparison
        double priceDifference =
                Math.abs(
                        product1.getPrice()
                                - product2.getPrice());

        Product cheaperProduct = null;

        if (product1.getPrice()
                < product2.getPrice()) {

            cheaperProduct = product1;

        } else if (product2.getPrice()
                < product1.getPrice()) {

            cheaperProduct = product2;
        }

        // Rating comparison
        double ratingDifference =
                Math.abs(
                        product1.getRating()
                                - product2.getRating());

        Product higherRatedProduct = null;

        if (product1.getRating()
                > product2.getRating()) {

            higherRatedProduct = product1;

        } else if (product2.getRating()
                > product1.getRating()) {

            higherRatedProduct = product2;
        }

        // Calculate evaluation scores
        double product1Score =
                productEvaluationService
                        .calculateScore(product1);

        double product2Score =
                productEvaluationService
                        .calculateScore(product2);

        // Generate comparison summary
        String comparisonSummary;

        if (product1Score > product2Score) {

            comparisonSummary =
                    product1.getName()
                            + " has a higher overall score than "
                            + product2.getName();

        } else if (product2Score > product1Score) {

            comparisonSummary =
                    product2.getName()
                            + " has a higher overall score than "
                            + product1.getName();

        } else {

            comparisonSummary =
                    "Both products have the same overall score";
        }

        return new ProductComparisonResponse(
                product1,
                product2,
                priceDifference,
                cheaperProduct,
                ratingDifference,
                higherRatedProduct,
                product1Score,
                product2Score,
                comparisonSummary);
    }
}
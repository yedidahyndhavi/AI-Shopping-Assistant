package com.aishopping.shoppingassistant.controller;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductRequirement;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;

import com.aishopping.shoppingassistant.repository.ProductRepository;
import com.aishopping.shoppingassistant.service.NaturalLanguageQueryService;
import com.aishopping.shoppingassistant.service.ProductRecommendationService;
import com.aishopping.shoppingassistant.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.aishopping.shoppingassistant.model.ComparisonInsight;
import com.aishopping.shoppingassistant.service.ComparisonInsightService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductRecommendationService productRecommendationService;
    private final NaturalLanguageQueryService naturalLanguageQueryService;
    private final ComparisonInsightService comparisonInsightService;

    public ProductController(
        ProductService productService,
        ProductRepository productRepository,
        ProductRecommendationService productRecommendationService,
        NaturalLanguageQueryService naturalLanguageQueryService,
        ComparisonInsightService comparisonInsightService) {

    this.productService = productService;
    this.productRepository = productRepository;
    this.productRecommendationService = productRecommendationService;
    this.naturalLanguageQueryService = naturalLanguageQueryService;
    this.comparisonInsightService = comparisonInsightService;
}

    // ============================================================
    // 1. GET ALL PRODUCTS
    // GET /api/products
    // ============================================================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ============================================================
    // 2. GET AVAILABLE PRODUCTS
    // GET /api/products/available
    // ============================================================

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {

        List<Product> products = productService.getAllProducts()
                .stream()
                .filter(Product::isAvailable)
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    // ============================================================
    // 3. SEARCH PRODUCTS BY NAME
    // GET /api/products/search?name=Samsung
    // ============================================================

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String name) {

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Product search name cannot be empty.");
        }

        String searchTerm = name.trim().toLowerCase();

        List<Product> products = productService.getAllProducts()
                .stream()
                .filter(product -> product.getName() != null)
                .filter(product -> product.getName()
                        .toLowerCase()
                        .contains(searchTerm))
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    // ============================================================
    // 4. FILTER PRODUCTS BY CATEGORY
    // GET /api/products/filter/category?category=Smartphone
    // ============================================================

    @GetMapping("/filter/category")
    public ResponseEntity<?> filterByCategory(
            @RequestParam String category) {

        if (category == null || category.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Category cannot be empty.");
        }

        List<Product> products = productService.getAllProducts()
                .stream()
                .filter(Product::isAvailable)
                .filter(product -> product.getCategory() != null)
                .filter(product -> product.getCategory()
                        .equalsIgnoreCase(category.trim()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    // ============================================================
    // 5. FILTER PRODUCTS BY MAXIMUM PRICE
    // GET /api/products/filter/price?maxPrice=50000
    // ============================================================

    @GetMapping("/filter/price")
    public ResponseEntity<?> filterByPrice(
            @RequestParam double maxPrice) {

        if (!Double.isFinite(maxPrice) || maxPrice < 0) {
            return ResponseEntity.badRequest()
                    .body("Maximum price must be a valid non-negative number.");
        }

        List<Product> products = productService.getAllProducts()
                .stream()
                .filter(Product::isAvailable)
                .filter(product -> product.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    // ============================================================
    // 6. FILTER PRODUCTS BY MINIMUM RATING
    // GET /api/products/filter/rating?minRating=4.0
    // ============================================================

    @GetMapping("/filter/rating")
    public ResponseEntity<?> filterByRating(
            @RequestParam double minRating) {

        if (!Double.isFinite(minRating)
                || minRating < 0
                || minRating > 5) {

            return ResponseEntity.badRequest()
                    .body("Minimum rating must be between 0 and 5.");
        }

        List<Product> products = productService.getAllProducts()
                .stream()
                .filter(Product::isAvailable)
                .filter(product -> product.getRating() >= minRating)
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }
    // GET /api/products/compare/insight/1/2
@GetMapping("/compare/insight/{product1Id}/{product2Id}")
public ResponseEntity<?> compareProductInsights(
        @PathVariable Long product1Id,
        @PathVariable Long product2Id) {

    if (product1Id.equals(product2Id)) {
        return ResponseEntity.badRequest()
                .body("Choose two different products to compare.");
    }

    try {
        ComparisonInsight insight =
                comparisonInsightService.generateInsight(
                        product1Id, product2Id);

        return ResponseEntity.ok(insight);

    } catch (IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}

    // ============================================================
    // 7. CREATE PRODUCT
    // POST /api/products
    // ============================================================

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestBody Product product) {

        if (product == null) {
            return ResponseEntity.badRequest()
                    .body("Product request cannot be empty.");
        }

        if (product.getName() == null
                || product.getName().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Product name is required.");
        }

        if (product.getCategory() == null
                || product.getCategory().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Product category is required.");
        }

        if (!Double.isFinite(product.getPrice())
                || product.getPrice() < 0) {

            return ResponseEntity.badRequest()
                    .body("Product price must be valid and non-negative.");
        }

        if (!Double.isFinite(product.getRating())
                || product.getRating() < 0
                || product.getRating() > 5) {

            return ResponseEntity.badRequest()
                    .body("Product rating must be between 0 and 5.");
        }

        Product savedProduct = productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    // ============================================================
    // 8. COMPARE TWO PRODUCTS
    // GET /api/products/compare?ids=1,2
    // ============================================================

    @GetMapping("/compare")
    public ResponseEntity<?> compareProducts(
            @RequestParam String ids) {

        if (ids == null || ids.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Provide two product IDs separated by a comma.");
        }

        String[] idValues = ids.split(",");

        if (idValues.length != 2) {
            return ResponseEntity.badRequest()
                    .body("Exactly two product IDs are required.");
        }

        try {
            Long firstId = Long.parseLong(idValues[0].trim());
            Long secondId = Long.parseLong(idValues[1].trim());

            if (firstId.equals(secondId)) {
                return ResponseEntity.badRequest()
                        .body("Choose two different products to compare.");
            }

            Optional<Product> firstResult =
                    productRepository.findById(firstId);

            Optional<Product> secondResult =
                    productRepository.findById(secondId);

            if (firstResult.isEmpty() || secondResult.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("One or both products were not found.");
            }

            Product first = firstResult.get();
            Product second = secondResult.get();

            Map<String, Object> comparison = new LinkedHashMap<>();

            comparison.put("product1", first);
            comparison.put("product2", second);

            double priceDifference =
                    Math.abs(first.getPrice() - second.getPrice());

            comparison.put("priceDifference", priceDifference);

            if (first.getPrice() < second.getPrice()) {
                comparison.put("cheaperProduct", first.getName());
            } else if (second.getPrice() < first.getPrice()) {
                comparison.put("cheaperProduct", second.getName());
            } else {
                comparison.put("cheaperProduct", "Both have the same price");
            }

            if (first.getRating() > second.getRating()) {
                comparison.put("higherRatedProduct", first.getName());
            } else if (second.getRating() > first.getRating()) {
                comparison.put("higherRatedProduct", second.getName());
            } else {
                comparison.put("higherRatedProduct", "Both have the same rating");
            }

            return ResponseEntity.ok(comparison);

        } catch (NumberFormatException exception) {
            return ResponseEntity.badRequest()
                    .body("Product IDs must be valid numeric IDs.");
        }
    }

    // ============================================================
    // 9. RECOMMEND BEST PRODUCT FROM STRUCTURED REQUIREMENTS
    // POST /api/products/recommend
    // ============================================================

    @PostMapping("/recommend")
    public ResponseEntity<?> recommendBestProduct(
            @RequestBody RecommendationRequest request) {

        try {
            RecommendationResponse response =
                    productRecommendationService.recommendBestProduct(request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());

        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        }
    }

    // ============================================================
    // 10. TOP-N RECOMMENDATIONS FROM STRUCTURED REQUIREMENTS
    // POST /api/products/recommend/top
    // ============================================================

    @PostMapping("/recommend/top")
    public ResponseEntity<?> recommendTopProducts(
            @RequestBody RecommendationRequest request,
            @RequestParam(defaultValue = "3") int limit) {

        if (limit <= 0 || limit > 10) {
            return ResponseEntity.badRequest()
                    .body("Limit must be between 1 and 10.");
        }

        try {
            return ResponseEntity.ok(
                    productRecommendationService
                            .recommendTopProducts(request, limit));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());

        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        }
    }

    // ============================================================
    // 11. NATURAL-LANGUAGE PRODUCT RECOMMENDATION
    // POST /api/products/recommend/query
    // ============================================================

    @PostMapping("/recommend/query")
    public ResponseEntity<?> recommendFromNaturalLanguage(
            @RequestBody Map<String, String> requestBody) {

        if (requestBody == null
                || requestBody.get("query") == null
                || requestBody.get("query").isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Provide a non-empty shopping query.");
        }

        String query = requestBody.get("query").trim();

        try {
            ProductRequirement requirement =
                    naturalLanguageQueryService.parseRequirements(query);

            RecommendationResponse response =
                    productRecommendationService
                            .recommendBestProduct(requirement);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());

        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        }
    }

    // ============================================================
    // 12. TOP-N NATURAL-LANGUAGE RECOMMENDATIONS
    // POST /api/products/recommend/query/top?limit=3
    // ============================================================

    @PostMapping("/recommend/query/top")
    public ResponseEntity<?> recommendTopFromNaturalLanguage(
            @RequestBody Map<String, String> requestBody,
            @RequestParam(defaultValue = "3") int limit) {

        if (requestBody == null
                || requestBody.get("query") == null
                || requestBody.get("query").isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Provide a non-empty shopping query.");
        }

        if (limit <= 0 || limit > 10) {
            return ResponseEntity.badRequest()
                    .body("Limit must be between 1 and 10.");
        }

        try {
            ProductRequirement requirement =
                    naturalLanguageQueryService.parseRequirements(
                            requestBody.get("query").trim());

            RecommendationListResponse response =
                    productRecommendationService
                            .recommendTopProducts(requirement, limit);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());

        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(exception.getMessage());
        }
    }
}
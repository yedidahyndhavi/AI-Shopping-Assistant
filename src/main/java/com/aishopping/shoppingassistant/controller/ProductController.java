package com.aishopping.shoppingassistant.controller;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;
import com.aishopping.shoppingassistant.model.NaturalLanguageQueryRequest;

import com.aishopping.shoppingassistant.service.ProductEvaluationService;
import com.aishopping.shoppingassistant.service.ProductRankingService;
import com.aishopping.shoppingassistant.service.ProductRecommendationService;
import com.aishopping.shoppingassistant.service.ProductService;
import com.aishopping.shoppingassistant.service.NaturalLanguageQueryService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductEvaluationService productEvaluationService;
    private final ProductRankingService productRankingService;
    private final ProductRecommendationService productRecommendationService;
    private final NaturalLanguageQueryService naturalLanguageQueryService;

    public ProductController(
            ProductService productService,
            ProductEvaluationService productEvaluationService,
            ProductRankingService productRankingService,
            ProductRecommendationService productRecommendationService,
            NaturalLanguageQueryService naturalLanguageQueryService) {

        this.productService = productService;
        this.productEvaluationService = productEvaluationService;
        this.productRankingService = productRankingService;
        this.productRecommendationService = productRecommendationService;
        this.naturalLanguageQueryService = naturalLanguageQueryService;
    }

    // =========================================================
    // DAY 22 - VALIDATION ERROR HANDLER
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(
            IllegalArgumentException exception) {

        return exception.getMessage();
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String name) {

        return productService.searchProducts(name);
    }

    // =========================================================
    // FILTER BY CATEGORY
    // =========================================================

    @GetMapping("/filter/category")
    public List<Product> filterByCategory(
            @RequestParam String category) {

        return productService.filterByCategory(category);
    }

    // =========================================================
    // FILTER BY MAXIMUM PRICE
    // =========================================================

    @GetMapping("/filter/price")
    public List<Product> filterByMaxPrice(
            @RequestParam double maxPrice) {

        return productService.filterByMaxPrice(maxPrice);
    }

    // =========================================================
    // FILTER BY MINIMUM RATING
    // =========================================================

    @GetMapping("/filter/rating")
    public List<Product> filterByMinRating(
            @RequestParam double minRating) {

        return productService.filterByMinRating(minRating);
    }

    // =========================================================
    // BASIC PRODUCT COMPARISON
    // =========================================================

    @GetMapping("/compare")
    public ProductComparison compareProducts(
            @RequestParam List<Long> ids) {

        return productService.compareProducts(ids);
    }

    // =========================================================
    // DETAILED PRODUCT COMPARISON
    // =========================================================

    @GetMapping("/compare/detailed")
    public ProductComparisonResponse compareProductsDetailed(
            @RequestParam List<Long> ids) {

        return productService.compareProductsDetailed(ids);
    }

    // =========================================================
    // PRODUCT SCORE
    // =========================================================

    @GetMapping("/{id}/score")
    public double getProductScore(
            @PathVariable Long id) {

        Product product = productService.getAllProducts()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return productEvaluationService.calculateScore(product);
    }

    // =========================================================
    // RANK ALL PRODUCTS
    // =========================================================

    @GetMapping("/rank")
    public List<Product> rankProducts() {

        List<Product> products =
                productService.getAllProducts();

        return productRankingService.rankProducts(products);
    }

    // =========================================================
    // RECOMMEND BEST PRODUCT
    // =========================================================

    @PostMapping("/recommend")
    public RecommendationResponse recommendBestProduct(
            @RequestBody RecommendationRequest request) {

        return productRecommendationService
                .recommendBestProduct(request);
    }

    // =========================================================
    // TOP-N RECOMMENDATIONS
    // =========================================================

    @PostMapping("/recommend/top")
    public RecommendationListResponse recommendTopProducts(
            @RequestBody RecommendationRequest request,
            @RequestParam(defaultValue = "3") int limit) {

        return productRecommendationService
                .recommendTopProducts(request, limit);
    }

    // =========================================================
    // PERSONALIZED RECOMMENDATION
    // =========================================================

    @PostMapping("/recommend/personalized")
    public Product recommendPersonalizedProduct(
            @RequestBody RecommendationRequest request) {

        return productRecommendationService
                .recommendPersonalizedProduct(request);
    }

    // =========================================================
    // NATURAL LANGUAGE RECOMMENDATION
    // =========================================================

    @PostMapping("/recommend/query")
    public RecommendationResponse recommendFromQuery(
            @RequestBody NaturalLanguageQueryRequest request) {

        RecommendationRequest preferences =
                naturalLanguageQueryService
                        .parseQuery(request.getQuery());

        return productRecommendationService
                .recommendBestProduct(preferences);
    }

    // =========================================================
    // ADD PRODUCT
    // =========================================================

    @PostMapping
    public Product addProduct(
            @RequestBody Product product) {

        return productService.addProduct(product);
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }
}
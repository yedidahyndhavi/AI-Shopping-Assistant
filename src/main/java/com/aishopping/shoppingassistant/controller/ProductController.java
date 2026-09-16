package com.aishopping.shoppingassistant.controller;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.service.NaturalLanguageQueryService;
import com.aishopping.shoppingassistant.service.ProductEvaluationService;
import com.aishopping.shoppingassistant.service.ProductService;
import org.springframework.web.bind.annotation.*;
import com.aishopping.shoppingassistant.service.ProductEvaluationService;
import com.aishopping.shoppingassistant.service.ProductRankingService;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.service.ProductRecommendationService;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import com.aishopping.shoppingassistant.model.NaturalLanguageQueryRequest;
import com.aishopping.shoppingassistant.service.NaturalLanguageQueryService;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductEvaluationService productEvaluationService;
    private final ProductRankingService productRankingService;
    private final ProductRecommendationService productRecommendationService;
    private final NaturalLanguageQueryService naturalLanguageQueryService;

   public ProductController(ProductService productService,
                         ProductEvaluationService productEvaluationService,
                         ProductRankingService productRankingService,
                         ProductRecommendationService productRecommendationService,
                         NaturalLanguageQueryService naturalLanguageQueryService){

    this.productService = productService;
    this.productEvaluationService = productEvaluationService;
    this.productRankingService = productRankingService;
    this.productRecommendationService = productRecommendationService;
    this.naturalLanguageQueryService = naturalLanguageQueryService;
}
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }
    @GetMapping("/filter/category")
    public List<Product> filterByCategory(@RequestParam String category) {
    return productService.filterByCategory(category);
}

    @GetMapping("/filter/price")
    public List<Product> filterByMaxPrice(@RequestParam double maxPrice) {
    return productService.filterByMaxPrice(maxPrice);
}

    @GetMapping("/filter/rating")
    public List<Product> filterByMinRating(@RequestParam double minRating) {
    return productService.filterByMinRating(minRating);
}

    @GetMapping("/compare")
    public ProductComparison compareProducts(@RequestParam List<Long> ids) {
        return productService.compareProducts(ids);
    }
    @GetMapping("/compare/detailed")
    public ProductComparisonResponse compareProductsDetailed(
        @RequestParam List<Long> ids) {

    return productService.compareProductsDetailed(ids);
}
    @GetMapping("/{id}/score")
    public double getProductScore(@PathVariable Long id) {

    Product product = productService.getAllProducts()
            .stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product not found"));

    return productEvaluationService.calculateScore(product);
}
    @GetMapping("/rank")
    public List<Product> rankProducts() {
    List<Product> products = productService.getAllProducts();

    return productRankingService.rankProducts(products);
}

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                             @RequestBody Product product) {
    return productService.updateProduct(id, product);
}
    @PostMapping("/recommend")
public RecommendationResponse recommendBestProduct(
        @RequestBody RecommendationRequest request) {

    return productRecommendationService.recommendBestProduct(request);
}
@PostMapping("/recommend/top")
public RecommendationListResponse recommendTopProducts(
        @RequestBody RecommendationRequest request,
        @RequestParam(defaultValue = "3") int limit) {

    return productRecommendationService
            .recommendTopProducts(request, limit);
}
@PostMapping("/recommend/query")
public RecommendationResponse recommendFromQuery(
        @RequestBody NaturalLanguageQueryRequest request) {

    RecommendationRequest preferences =
            naturalLanguageQueryService.parseQuery(request.getQuery());

    return productRecommendationService
            .recommendBestProduct(preferences);
}
@PostMapping("/recommend/personalized")
public Product recommendPersonalizedProduct(
        @RequestBody RecommendationRequest request) {

    return productRecommendationService
            .recommendPersonalizedProduct(request);
}
}
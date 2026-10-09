package com.aishopping.shoppingassistant.controller;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;
import com.aishopping.shoppingassistant.model.ProductRequirement;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;
import com.aishopping.shoppingassistant.model.NaturalLanguageQueryRequest;
import com.aishopping.shoppingassistant.model.ComparisonInsight;

import com.aishopping.shoppingassistant.service.ProductEvaluationService;
import com.aishopping.shoppingassistant.service.ProductRankingService;
import com.aishopping.shoppingassistant.service.ProductRecommendationService;
import com.aishopping.shoppingassistant.service.ProductService;
import com.aishopping.shoppingassistant.service.NaturalLanguageQueryService;
import com.aishopping.shoppingassistant.service.ProductSpecificationService;
import com.aishopping.shoppingassistant.service.ComparisonInsightService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        },
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        },
        allowedHeaders = "*"
)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductEvaluationService productEvaluationService;
    private final ProductRankingService productRankingService;
    private final ProductRecommendationService productRecommendationService;
    private final NaturalLanguageQueryService naturalLanguageQueryService;
    private final ProductSpecificationService productSpecificationService;
    private final ComparisonInsightService comparisonInsightService;

    public ProductController(
            ProductService productService,
            ProductEvaluationService productEvaluationService,
            ProductRankingService productRankingService,
            ProductRecommendationService productRecommendationService,
            NaturalLanguageQueryService naturalLanguageQueryService,
            ProductSpecificationService productSpecificationService,
            ComparisonInsightService comparisonInsightService) {

        this.productService = productService;
        this.productEvaluationService = productEvaluationService;
        this.productRankingService = productRankingService;
        this.productRecommendationService = productRecommendationService;
        this.naturalLanguageQueryService = naturalLanguageQueryService;
        this.productSpecificationService = productSpecificationService;
        this.comparisonInsightService = comparisonInsightService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(
            IllegalArgumentException exception) {
        return exception.getMessage();
    }

    // PRODUCT ENDPOINTS

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/available")
    public List<Product> getAvailableProducts() {
        return productService.getAvailableProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    @GetMapping("/filter/category")
    public List<Product> filterByCategory(
            @RequestParam String category) {
        return productService.filterByCategory(category);
    }

    @GetMapping("/filter/price")
    public List<Product> filterByMaxPrice(
            @RequestParam double maxPrice) {
        return productService.filterByMaxPrice(maxPrice);
    }

    @GetMapping("/filter/rating")
    public List<Product> filterByMinRating(
            @RequestParam double minRating) {
        return productService.filterByMinRating(minRating);
    }

    // PRODUCT COMPARISON

    @GetMapping("/compare")
    public ProductComparison compareProducts(
            @RequestParam List<Long> ids) {
        return productService.compareProducts(ids);
    }

    @GetMapping("/compare/detailed")
    public ProductComparisonResponse compareProductsDetailed(
            @RequestParam List<Long> ids) {
        return productService.compareProductsDetailed(ids);
    }

    // DAY 39 - AUTOMATIC COMPARISON INSIGHTS

    @GetMapping("/compare/insights")
    public ComparisonInsight compareInsights(
            @RequestParam List<Long> ids) {

        if (ids == null || ids.size() != 2) {
            throw new IllegalArgumentException(
                    "Exactly two product IDs are required");
        }

        return comparisonInsightService.generateInsight(
                ids.get(0),
                ids.get(1));
    }

    // PRODUCT SCORE

    @GetMapping("/{id}/score")
    public double getProductScore(@PathVariable Long id) {
        Product product = productService.getAllProducts()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return productEvaluationService.calculateScore(product);
    }

    // PRODUCT RANKING

    @GetMapping("/rank")
    public List<Product> rankProducts() {
        return productRankingService.rankProducts(
                productService.getAllProducts());
    }

    // RECOMMENDATION ENDPOINTS

    @PostMapping("/recommend")
    public RecommendationResponse recommendBestProduct(
            @RequestBody RecommendationRequest request) {
        return productRecommendationService
                .recommendBestProduct(request);
    }

    @PostMapping("/recommend/top")
    public RecommendationListResponse recommendTopProducts(
            @RequestBody RecommendationRequest request,
            @RequestParam(defaultValue = "3") int limit) {
        return productRecommendationService
                .recommendTopProducts(request, limit);
    }

    @PostMapping("/recommend/personalized")
    public Product recommendPersonalizedProduct(
            @RequestBody RecommendationRequest request) {
        return productRecommendationService
                .recommendPersonalizedProduct(request);
    }

    // DAY 37 - NATURAL LANGUAGE BEST RECOMMENDATION

    @PostMapping("/recommend/query")
    public RecommendationResponse recommendFromQuery(
            @RequestBody NaturalLanguageQueryRequest request) {

        ProductRequirement requirement =
                naturalLanguageQueryService.parseRequirements(
                        request.getQuery());

        return productRecommendationService
                .recommendBestProduct(requirement);
    }

    // DAY 38 - NATURAL LANGUAGE TOP 3 RECOMMENDATIONS

    @PostMapping("/recommend/query/top")
    public RecommendationListResponse recommendTopFromQuery(
            @RequestBody NaturalLanguageQueryRequest request) {

        ProductRequirement requirement =
                naturalLanguageQueryService.parseRequirements(
                        request.getQuery());

        return productRecommendationService
                .recommendTopProducts(requirement, 3);
    }

    // PRODUCT SPECIFICATIONS

    @PostMapping("/{productId}/specifications")
    public java.util.Map<String, Object> addSpecification(
            @PathVariable Long productId,
            @RequestBody ProductSpecification specification) {

        ProductSpecification saved =
                productSpecificationService.addSpecification(
                        productId, specification);

        java.util.Map<String, Object> response =
                new java.util.LinkedHashMap<>();

        response.put("id", saved.getId());
        response.put("specificationName",
                saved.getSpecificationName());
        response.put("specificationValue",
                saved.getSpecificationValue());
        response.put("unit", saved.getUnit());
        response.put("numericValue",
                saved.getNumericValue());

        return response;
    }

    @GetMapping("/{productId}/specifications")
    public List<ProductSpecification> getSpecifications(
            @PathVariable Long productId) {
        return productSpecificationService
                .getSpecifications(productId);
    }

    @GetMapping("/specifications/{specificationId}")
    public ProductSpecification getSpecification(
            @PathVariable Long specificationId) {
        return productSpecificationService
                .getSpecification(specificationId);
    }

    @PutMapping("/specifications/{specificationId}")
    public ProductSpecification updateSpecification(
            @PathVariable Long specificationId,
            @RequestBody ProductSpecification specification) {
        return productSpecificationService
                .updateSpecification(specificationId, specification);
    }

    @DeleteMapping("/specifications/{specificationId}")
    public String deleteSpecification(
            @PathVariable Long specificationId) {
        productSpecificationService
                .deleteSpecification(specificationId);
        return "Specification deleted successfully";
    }

    // ADD / UPDATE PRODUCTS

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    // BULK SPECIFICATION UPDATE

    @PostMapping("/{productId}/specifications/bulk")
    public String addOrUpdateSpecifications(
            @PathVariable Long productId,
            @RequestBody List<ProductSpecification> specifications) {

        productSpecificationService.addOrUpdateSpecifications(
                productId, specifications);

        return "Specifications added/updated successfully";
    }
}
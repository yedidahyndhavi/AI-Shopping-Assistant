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

import com.aishopping.shoppingassistant.service.ProductEvaluationService;
import com.aishopping.shoppingassistant.service.ProductRankingService;
import com.aishopping.shoppingassistant.service.ProductRecommendationService;
import com.aishopping.shoppingassistant.service.ProductService;
import com.aishopping.shoppingassistant.service.NaturalLanguageQueryService;
import com.aishopping.shoppingassistant.service.ProductSpecificationService;

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

    private final ProductEvaluationService
            productEvaluationService;

    private final ProductRankingService
            productRankingService;

    private final ProductRecommendationService
            productRecommendationService;

    private final NaturalLanguageQueryService
            naturalLanguageQueryService;

    private final ProductSpecificationService
            productSpecificationService;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ProductController(
            ProductService productService,
            ProductEvaluationService productEvaluationService,
            ProductRankingService productRankingService,
            ProductRecommendationService productRecommendationService,
            NaturalLanguageQueryService naturalLanguageQueryService,
            ProductSpecificationService productSpecificationService) {

        this.productService =
                productService;

        this.productEvaluationService =
                productEvaluationService;

        this.productRankingService =
                productRankingService;

        this.productRecommendationService =
                productRecommendationService;

        this.naturalLanguageQueryService =
                naturalLanguageQueryService;

        this.productSpecificationService =
                productSpecificationService;
    }


    // ============================================================
    // EXCEPTION HANDLING
    // ============================================================

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(
            IllegalArgumentException exception) {

        return exception.getMessage();
    }


    // ============================================================
    // PRODUCT ENDPOINTS
    // ============================================================

    // Get all products
    @GetMapping
    public List<Product> getAllProducts() {

        return productService.getAllProducts();
    }


    // Get only available products
    @GetMapping("/available")
    public List<Product> getAvailableProducts() {

        return productService.getAvailableProducts();
    }


    // Search products by name
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String name) {

        return productService.searchProducts(name);
    }


    // Filter products by category
    @GetMapping("/filter/category")
    public List<Product> filterByCategory(
            @RequestParam String category) {

        return productService.filterByCategory(category);
    }


    // Filter products by maximum price
    @GetMapping("/filter/price")
    public List<Product> filterByMaxPrice(
            @RequestParam double maxPrice) {

        return productService.filterByMaxPrice(maxPrice);
    }


    // Filter products by minimum rating
    @GetMapping("/filter/rating")
    public List<Product> filterByMinRating(
            @RequestParam double minRating) {

        return productService.filterByMinRating(minRating);
    }


    // ============================================================
    // PRODUCT COMPARISON
    // ============================================================

    // Basic comparison
    @GetMapping("/compare")
    public ProductComparison compareProducts(
            @RequestParam List<Long> ids) {

        return productService.compareProducts(ids);
    }


    // Detailed specification-aware comparison
    @GetMapping("/compare/detailed")
    public ProductComparisonResponse compareProductsDetailed(
            @RequestParam List<Long> ids) {

        return productService.compareProductsDetailed(ids);
    }


    // ============================================================
    // PRODUCT SCORE
    // ============================================================

    @GetMapping("/{id}/score")
    public double getProductScore(
            @PathVariable Long id) {

        Product product =
                productService
                        .getAllProducts()
                        .stream()
                        .filter(productItem ->
                                productItem.getId()
                                        .equals(id))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));

        return productEvaluationService
                .calculateScore(product);
    }


    // ============================================================
    // PRODUCT RANKING
    // ============================================================

    @GetMapping("/rank")
    public List<Product> rankProducts() {

        List<Product> products =
                productService.getAllProducts();

        return productRankingService
                .rankProducts(products);
    }


    // ============================================================
    // RECOMMENDATION ENDPOINTS
    // ============================================================

    // Recommend best product using RecommendationRequest
    @PostMapping("/recommend")
    public RecommendationResponse recommendBestProduct(
            @RequestBody RecommendationRequest request) {

        return productRecommendationService
                .recommendBestProduct(request);
    }


    // Recommend top products using RecommendationRequest
    @PostMapping("/recommend/top")
    public RecommendationListResponse recommendTopProducts(
            @RequestBody RecommendationRequest request,
            @RequestParam(defaultValue = "3") int limit) {

        return productRecommendationService
                .recommendTopProducts(
                        request,
                        limit);
    }


    // Personalized recommendation
    @PostMapping("/recommend/personalized")
    public Product recommendPersonalizedProduct(
            @RequestBody RecommendationRequest request) {

        return productRecommendationService
                .recommendPersonalizedProduct(request);
    }


    // ============================================================
    // DAY 37
    // NATURAL LANGUAGE RECOMMENDATION
    // ============================================================

    @PostMapping("/recommend/query")
    public RecommendationResponse recommendFromQuery(
            @RequestBody NaturalLanguageQueryRequest request) {

        /*
         * Step 1:
         * Convert the user's natural-language query
         * into structured requirements.
         */

        ProductRequirement requirement =
                naturalLanguageQueryService
                        .parseRequirements(
                                request.getQuery());


        /*
         * Step 2:
         * Use the structured requirements to filter
         * and rank actual products.
         */

        return productRecommendationService
                .recommendBestProduct(
                        requirement);
    }


    // ============================================================
    // DAY 37
    // REQUIREMENT EXTRACTION TEST ENDPOINT
    // ============================================================

    @PostMapping("/requirements")
    public ProductRequirement extractProductRequirements(
            @RequestBody NaturalLanguageQueryRequest request) {

        return naturalLanguageQueryService
                .parseRequirements(
                        request.getQuery());
    }


    // ============================================================
    // PRODUCT SPECIFICATION ENDPOINTS
    // ============================================================

    // Add specification to product
    @PostMapping("/{productId}/specifications")
    public java.util.Map<String, Object> addSpecification(
            @PathVariable Long productId,
            @RequestBody ProductSpecification specification) {

        ProductSpecification savedSpecification =
                productSpecificationService
                        .addSpecification(
                                productId,
                                specification);

        java.util.Map<String, Object> response =
                new java.util.LinkedHashMap<>();

        response.put(
                "id",
                savedSpecification.getId());

        response.put(
                "specificationName",
                savedSpecification
                        .getSpecificationName());

        response.put(
                "specificationValue",
                savedSpecification
                        .getSpecificationValue());

        response.put(
                "unit",
                savedSpecification.getUnit());

        response.put(
                "numericValue",
                savedSpecification
                        .getNumericValue());

        return response;
    }


    // Get specifications for a product
    @GetMapping("/{productId}/specifications")
    public List<ProductSpecification> getSpecifications(
            @PathVariable Long productId) {

        return productSpecificationService
                .getSpecifications(productId);
    }


    // Get specification by ID
    @GetMapping("/specifications/{specificationId}")
    public ProductSpecification getSpecification(
            @PathVariable Long specificationId) {

        return productSpecificationService
                .getSpecification(
                        specificationId);
    }


    // Update specification
    @PutMapping("/specifications/{specificationId}")
    public ProductSpecification updateSpecification(
            @PathVariable Long specificationId,
            @RequestBody ProductSpecification specification) {

        return productSpecificationService
                .updateSpecification(
                        specificationId,
                        specification);
    }


    // Delete specification
    @DeleteMapping("/specifications/{specificationId}")
    public String deleteSpecification(
            @PathVariable Long specificationId) {

        productSpecificationService
                .deleteSpecification(
                        specificationId);

        return "Specification deleted successfully";
    }


    // ============================================================
    // ADD / UPDATE PRODUCTS
    // ============================================================

    // Add product
    @PostMapping
    public Product addProduct(
            @RequestBody Product product) {

        return productService.addProduct(product);
    }


    // Update product
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(
                id,
                product);
    }


    // ============================================================
    // BULK SPECIFICATION UPDATE
    // ============================================================

    @PostMapping("/{productId}/specifications/bulk")
    public String addOrUpdateSpecifications(
            @PathVariable Long productId,
            @RequestBody List<ProductSpecification> specifications) {

        productSpecificationService
                .addOrUpdateSpecifications(
                        productId,
                        specifications);

        return "Specifications added/updated successfully";
    }
}
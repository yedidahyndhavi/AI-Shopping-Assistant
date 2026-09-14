package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {

    private final ProductService productService;
    private final ProductRankingService productRankingService;
    private final ProductEvaluationService productEvaluationService;

    public ProductRecommendationService(
            ProductService productService,
            ProductRankingService productRankingService,
            ProductEvaluationService productEvaluationService) {

        this.productService = productService;
        this.productRankingService = productRankingService;
        this.productEvaluationService = productEvaluationService;
    }

    public RecommendationResponse recommendBestProduct(
            RecommendationRequest request) {

        List<Product> products = productService.getAllProducts();

        List<Product> matchingProducts = products.stream()
                .filter(product ->
                        product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .filter(product ->
                        product.getPrice() <= request.getMaxPrice())
                .filter(product ->
                        product.getRating() >= request.getMinRating())
                .collect(Collectors.toList());

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException(
                    "No products match the given preferences");
        }

        List<Product> rankedProducts =
                productRankingService.rankProducts(matchingProducts);

        Product recommendedProduct = rankedProducts.get(0);

        double score =
                productEvaluationService.calculateScore(recommendedProduct);

        String reason =
                "Best product matching your category, budget, and minimum rating preferences";

        return new RecommendationResponse(
                recommendedProduct,
                score,
                reason
        );
    }
}
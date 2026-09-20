package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.RecommendationExplanation;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;

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

    // =========================================================
    // RECOMMEND BEST PRODUCT
    // =========================================================

    public RecommendationResponse recommendBestProduct(
            RecommendationRequest request) {

        List<Product> products =
                productService.getAllProducts();

        List<Product> matchingProducts = products.stream()
                .filter(product ->
                        product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .filter(product ->
                        product.getPrice() <= request.getMaxPrice())
                .filter(product ->
                        product.getRating() >= request.getMinRating())
                .collect(Collectors.toList());

        // -----------------------------------------------------
        // DAY 18 - FALLBACK FOR NO EXACT MATCH
        // -----------------------------------------------------

        if (matchingProducts.isEmpty()) {

            List<Product> alternatives =
                    findAlternativeProducts(request);

            if (alternatives.isEmpty()) {
                throw new RuntimeException(
                        "No products found for the requested category");
            }

            Product alternativeProduct =
                    alternatives.get(0);

            double score =
                    productEvaluationService.calculateScore(
                            alternativeProduct);

            String reason =
                    "No product matched all your preferences. "
                    + "Showing the closest available alternative.";

            String budgetMessage =
                    "The recommended alternative costs ₹"
                    + alternativeProduct.getPrice()
                    + " compared with your maximum budget of ₹"
                    + request.getMaxPrice();

            String ratingMessage =
                    "The alternative has a rating of "
                    + alternativeProduct.getRating()
                    + " compared with your minimum rating requirement of "
                    + request.getMinRating();

            String preferenceMessage =
                    "This product was selected as the closest available "
                    + "alternative to your requested preferences";

            RecommendationExplanation explanation =
                    new RecommendationExplanation(
                            budgetMessage,
                            ratingMessage,
                            preferenceMessage
                    );

            return new RecommendationResponse(
                    alternativeProduct,
                    score,
                    reason,
                    explanation
            );
        }

        // -----------------------------------------------------
        // RANK MATCHING PRODUCTS
        // -----------------------------------------------------

        List<Product> rankedProducts =
        new java.util.ArrayList<>(
                productRankingService.rankProducts(
                        matchingProducts));

if (request.getPreferredBrand() != null
        && !request.getPreferredBrand().isBlank()) {

    rankedProducts.sort((product1, product2) -> {

        boolean product1Matches =
                product1.getBrand().equalsIgnoreCase(
                        request.getPreferredBrand());

        boolean product2Matches =
                product2.getBrand().equalsIgnoreCase(
                        request.getPreferredBrand());

        if (product1Matches && !product2Matches) {
            return -1;
        }

        if (!product1Matches && product2Matches) {
            return 1;
        }

        return 0;
    });
}

        Product recommendedProduct =
                rankedProducts.get(0);

        double score =
                productEvaluationService.calculateScore(
                        recommendedProduct);

        String reason =
                "Best product matching your category, budget, "
                + "and minimum rating preferences";

        // -----------------------------------------------------
        // STRUCTURED EXPLANATION
        // -----------------------------------------------------

        String budgetMessage =
                "Price ₹"
                + recommendedProduct.getPrice()
                + " is within your maximum budget of ₹"
                + request.getMaxPrice();

        String ratingMessage =
                "Rating "
                + recommendedProduct.getRating()
                + " meets your minimum rating requirement of "
                + request.getMinRating();

        String preferenceMessage;

        if (request.getPriceWeight()
                > request.getRatingWeight()) {

            preferenceMessage =
                    "Recommendation gives higher importance "
                    + "to your price preference";

        } else if (request.getRatingWeight()
                > request.getPriceWeight()) {

            preferenceMessage =
                    "Recommendation gives higher importance "
                    + "to your rating preference";

        } else {

            preferenceMessage =
                    "Recommendation gives equal importance "
                    + "to price and rating";
        }

        RecommendationExplanation explanation =
                new RecommendationExplanation(
                        budgetMessage,
                        ratingMessage,
                        preferenceMessage
                );

        return new RecommendationResponse(
                recommendedProduct,
                score,
                reason,
                explanation
        );
    }

    // =========================================================
    // DAY 18 - FIND ALTERNATIVE PRODUCTS
    // =========================================================

    private List<Product> findAlternativeProducts(
            RecommendationRequest request) {

        List<Product> products =
                productService.getAllProducts();

        return products.stream()
                .filter(product ->
                        product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .sorted((product1, product2) -> {

                    double difference1 =
                            Math.abs(
                                    product1.getPrice()
                                    - request.getMaxPrice())
                            + Math.abs(
                                    product1.getRating()
                                    - request.getMinRating())
                            * 10000;

                    double difference2 =
                            Math.abs(
                                    product2.getPrice()
                                    - request.getMaxPrice())
                            + Math.abs(
                                    product2.getRating()
                                    - request.getMinRating())
                            * 10000;

                    return Double.compare(
                            difference1,
                            difference2);
                })
                .limit(3)
                .collect(Collectors.toList());
    }

    // =========================================================
    // RECOMMEND TOP PRODUCTS
    // =========================================================

    public RecommendationListResponse recommendTopProducts(
            RecommendationRequest request,
            int limit) {

        List<Product> products =
                productService.getAllProducts();

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
                productRankingService.rankProducts(
                        matchingProducts);

        List<RecommendationResponse> recommendations =
                rankedProducts.stream()
                        .limit(limit)
                        .map(product -> {

                            double score =
                                    productEvaluationService
                                            .calculateScore(product);

                            String reason =
                                    "Matches your category, budget, "
                                    + "and minimum rating preferences";

                            return new RecommendationResponse(
                                    product,
                                    score,
                                    reason
                            );
                        })
                        .collect(Collectors.toList());

        return new RecommendationListResponse(
                recommendations);
    }

    // =========================================================
    // PERSONALIZED PRODUCT RECOMMENDATION
    // =========================================================

    public Product recommendPersonalizedProduct(
            RecommendationRequest request) {

        List<Product> products =
                productService.getAllProducts();

        List<Product> matchingProducts = products.stream()
                .filter(product ->
                        product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .filter(product ->
                        product.getPrice() <= request.getMaxPrice())
                .filter(product ->
                        product.getRating() >= request.getMinRating())
                .sorted((product1, product2) -> {

                    double score1 =
                            productEvaluationService
                                    .calculatePersonalizedScore(
                                            product1,
                                            request.getPriceWeight(),
                                            request.getRatingWeight());

                    double score2 =
                            productEvaluationService
                                    .calculatePersonalizedScore(
                                            product2,
                                            request.getPriceWeight(),
                                            request.getRatingWeight());

                    return Double.compare(
                            score2,
                            score1);
                })
                .collect(Collectors.toList());

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match the given preferences");
        }

        return matchingProducts.get(0);
    }
}
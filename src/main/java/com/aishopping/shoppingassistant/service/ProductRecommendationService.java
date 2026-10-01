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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

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

        request.validate();

        List<Product> products =
                productService.getAllProducts();


        // =====================================================
        // FILTER PRODUCTS
        // =====================================================

        List<Product> matchingProducts = products.stream()

                // Category
                .filter(product ->
                        product.getCategory()
                                .equalsIgnoreCase(
                                        request.getCategory()))

                // Availability
                .filter(Product::isAvailable)

                // Maximum price
                .filter(product ->
                        product.getPrice()
                                <= request.getMaxPrice())

                // Minimum rating
                .filter(product ->
                        product.getRating()
                                >= request.getMinRating())

                .collect(Collectors.toList());


        // =====================================================
        // NO EXACT MATCH
        // =====================================================

        /*
         * IMPORTANT:
         *
         * Do not recommend a product outside the user's
         * requested budget or rating.
         *
         * Example:
         *
         * User:
         * "Laptop under 20000 with rating above 4"
         *
         * If no product satisfies those requirements,
         * return an error instead of recommending a
         * 99999 laptop.
         */

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your requirements. "
                    + "Try increasing your budget or "
                    + "changing your requirements."
            );
        }


        // =====================================================
        // RANK MATCHING PRODUCTS
        // =====================================================

        List<Product> rankedProducts =
                new java.util.ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts));


        // =====================================================
        // PREFERRED BRAND
        // =====================================================

        if (request.getPreferredBrand() != null
                && !request.getPreferredBrand().isBlank()) {

            rankedProducts.sort((product1, product2) -> {

                boolean product1Matches =
                        product1.getBrand()
                                .equalsIgnoreCase(
                                        request.getPreferredBrand());

                boolean product2Matches =
                        product2.getBrand()
                                .equalsIgnoreCase(
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


        // =====================================================
        // SELECT BEST PRODUCT
        // =====================================================

        Product recommendedProduct =
                rankedProducts.get(0);


        // =====================================================
        // CALCULATE SCORE
        // =====================================================

        double score =
                productEvaluationService
                        .calculateScore(
                                recommendedProduct);


        // =====================================================
        // RECOMMENDATION REASON
        // =====================================================

        String reason =
                "Best product matching your category, "
                + "budget, and minimum rating preferences";


        // =====================================================
        // BUDGET EXPLANATION
        // =====================================================

        String budgetMessage =
                "Price ₹"
                + formatPrice(
                        recommendedProduct.getPrice())
                + " is within your maximum budget of ₹"
                + formatPrice(
                        request.getMaxPrice());


        // =====================================================
        // RATING EXPLANATION
        // =====================================================

        String ratingMessage =
                "Rating "
                + recommendedProduct.getRating()
                + " meets your minimum rating requirement of "
                + request.getMinRating();


        // =====================================================
        // PREFERENCE EXPLANATION
        // =====================================================

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


        // =====================================================
        // BRAND EXPLANATION
        // =====================================================

        String brandMessage;

        if (request.getPreferredBrand() != null
                && !request.getPreferredBrand().isBlank()) {

            if (recommendedProduct.getBrand()
                    .equalsIgnoreCase(
                            request.getPreferredBrand())) {

                brandMessage =
                        "The recommended product matches "
                        + "your preferred brand: "
                        + request.getPreferredBrand();

            } else {

                brandMessage =
                        "No product from your preferred brand "
                        + "matched all your requirements";
            }

        } else {

            brandMessage =
                    "No specific brand preference was provided";
        }


        // =====================================================
        // CREATE EXPLANATION
        // =====================================================

        RecommendationExplanation explanation =
                new RecommendationExplanation(
                        budgetMessage,
                        ratingMessage,
                        preferenceMessage,
                        brandMessage
                );


        // =====================================================
        // RETURN RECOMMENDATION
        // =====================================================

        return new RecommendationResponse(
                recommendedProduct,
                score,
                reason,
                explanation
        );
    }


    // =========================================================
    // FORMAT PRICE
    // =========================================================

    private String formatPrice(double price) {

        return String.format(
                "%.0f",
                price
        );
    }


    // =========================================================
    // TOP-N PRODUCT RECOMMENDATIONS
    // =========================================================

    public RecommendationListResponse recommendTopProducts(
            RecommendationRequest request,
            int limit) {

        request.validate();


        // =====================================================
        // VALIDATE LIMIT
        // =====================================================

        if (limit <= 0) {

            throw new IllegalArgumentException(
                    "Recommendation limit must be greater than 0"
            );
        }

        if (limit > 10) {

            throw new IllegalArgumentException(
                    "Recommendation limit cannot exceed 10"
            );
        }


        // =====================================================
        // GET PRODUCTS
        // =====================================================

        List<Product> products =
                productService.getAllProducts();


        // =====================================================
        // FILTER PRODUCTS
        // =====================================================

        List<Product> matchingProducts =
                products.stream()

                        // Category
                        .filter(product ->
                                product.getCategory()
                                        .equalsIgnoreCase(
                                                request.getCategory()))

                        // Availability
                        .filter(Product::isAvailable)

                        // Budget
                        .filter(product ->
                                product.getPrice()
                                        <= request.getMaxPrice())

                        // Rating
                        .filter(product ->
                                product.getRating()
                                        >= request.getMinRating())

                        .collect(Collectors.toList());


        // =====================================================
        // NO MATCHING PRODUCTS
        // =====================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your requirements. "
                    + "Try increasing your budget or "
                    + "changing your requirements."
            );
        }


        // =====================================================
        // RANK PRODUCTS
        // =====================================================

        List<Product> rankedProducts =
                productRankingService
                        .rankProducts(
                                matchingProducts);


        // =====================================================
        // CREATE RECOMMENDATIONS
        // =====================================================

        List<RecommendationResponse> recommendations =
                rankedProducts.stream()

                        .limit(limit)

                        .map(product -> {

                            double score =
                                    productEvaluationService
                                            .calculateScore(
                                                    product);

                            String reason =
                                    "Matches your category, "
                                    + "budget, and minimum "
                                    + "rating preferences";

                            return new RecommendationResponse(
                                    product,
                                    score,
                                    reason
                            );
                        })

                        .collect(Collectors.toList());


        // =====================================================
        // RETURN TOP-N
        // =====================================================

        return new RecommendationListResponse(
                recommendations
        );
    }


    // =========================================================
    // PERSONALIZED PRODUCT RECOMMENDATION
    // =========================================================

    public Product recommendPersonalizedProduct(
            RecommendationRequest request) {

        request.validate();


        // =====================================================
        // GET PRODUCTS
        // =====================================================

        List<Product> products =
                productService.getAllProducts();


        // =====================================================
        // FILTER PRODUCTS
        // =====================================================

        List<Product> matchingProducts =
                products.stream()

                        // Category
                        .filter(product ->
                                product.getCategory()
                                        .equalsIgnoreCase(
                                                request.getCategory()))

                        // Availability
                        .filter(Product::isAvailable)

                        // Maximum price
                        .filter(product ->
                                product.getPrice()
                                        <= request.getMaxPrice())

                        // Minimum rating
                        .filter(product ->
                                product.getRating()
                                        >= request.getMinRating())

                        .collect(Collectors.toList());


        // =====================================================
        // NO MATCHING PRODUCTS
        // =====================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your requirements. "
                    + "Try increasing your budget or "
                    + "changing your requirements."
            );
        }


        // =====================================================
        // PREFERENCE-AWARE RANKING
        // =====================================================

        List<Product> rankedProducts =
                productRankingService
                        .rankProductsByPreference(
                                matchingProducts,
                                request.getPriceWeight(),
                                request.getRatingWeight()
                        );


        // =====================================================
        // RETURN BEST PERSONALIZED PRODUCT
        // =====================================================

        return rankedProducts.get(0);
    }
}
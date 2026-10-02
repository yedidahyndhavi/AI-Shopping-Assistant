package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.RecommendationExplanation;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        // RANK PRODUCTS
        // =====================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts)
                );


        // =====================================================
        // APPLY PREFERRED BRAND
        // =====================================================

        applyPreferredBrandPriority(
                rankedProducts,
                request.getPreferredBrand()
        );


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
        // RETURN RESPONSE
        // =====================================================

        return new RecommendationResponse(
                recommendedProduct,
                score,
                reason,
                explanation
        );
    }


    // =========================================================
    // TOP-N RECOMMENDATIONS
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
        // RANK PRODUCTS
        // =====================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts)
                );


        // =====================================================
        // APPLY PREFERRED BRAND PRIORITY
        // =====================================================

        /*
         * If the user selected a preferred brand,
         * products from that brand are moved ahead
         * of other matching brands.
         *
         * Example:
         *
         * Preferred brand = Apple
         *
         * Apple products will receive priority
         * in the Top-N ordering.
         */

        applyPreferredBrandPriority(
                rankedProducts,
                request.getPreferredBrand()
        );


        // =====================================================
        // CREATE TOP-N RECOMMENDATIONS
        // =====================================================

        List<RecommendationResponse> recommendations =
                rankedProducts.stream()

                        .limit(limit)

                        .map(product -> {

                            double score =
                                    productEvaluationService
                                            .calculateScore(
                                                    product);

                            String reason;

                            if (request.getPreferredBrand() != null
                                    && !request
                                    .getPreferredBrand()
                                    .isBlank()
                                    && product.getBrand()
                                    .equalsIgnoreCase(
                                            request
                                            .getPreferredBrand())) {

                                reason =
                                        "Matches your category, "
                                        + "budget, minimum rating, "
                                        + "and preferred brand";

                            } else {

                                reason =
                                        "Matches your category, "
                                        + "budget, and minimum "
                                        + "rating preferences";
                            }

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
    // PERSONALIZED RECOMMENDATION
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
        // PERSONALIZED RANKING
        // =====================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProductsByPreference(
                                        matchingProducts,
                                        request.getPriceWeight(),
                                        request.getRatingWeight()
                                )
                );


        // =====================================================
        // APPLY PREFERRED BRAND PRIORITY
        // =====================================================

        applyPreferredBrandPriority(
                rankedProducts,
                request.getPreferredBrand()
        );


        // =====================================================
        // RETURN BEST PRODUCT
        // =====================================================

        return rankedProducts.get(0);
    }


    // =========================================================
    // PREFERRED BRAND PRIORITY
    // =========================================================

    private void applyPreferredBrandPriority(
            List<Product> products,
            String preferredBrand) {

        if (preferredBrand == null
                || preferredBrand.isBlank()) {

            return;
        }


        products.sort((product1, product2) -> {

            boolean product1Matches =
                    product1.getBrand()
                            .equalsIgnoreCase(
                                    preferredBrand);

            boolean product2Matches =
                    product2.getBrand()
                            .equalsIgnoreCase(
                                    preferredBrand);


            // Product 1 is preferred brand
            if (product1Matches
                    && !product2Matches) {

                return -1;
            }


            // Product 2 is preferred brand
            if (!product1Matches
                    && product2Matches) {

                return 1;
            }


            // Same brand preference status
            return 0;
        });
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
}
package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductRequirement;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.model.RecommendationExplanation;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import com.aishopping.shoppingassistant.model.RecommendationResponse;
import com.aishopping.shoppingassistant.model.RecommendationListResponse;

import com.aishopping.shoppingassistant.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {

    private final ProductService productService;

    private final ProductRankingService productRankingService;

    private final ProductEvaluationService productEvaluationService;

    private final ProductSpecificationRepository
            productSpecificationRepository;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ProductRecommendationService(
            ProductService productService,
            ProductRankingService productRankingService,
            ProductEvaluationService productEvaluationService,
            ProductSpecificationRepository
                    productSpecificationRepository) {

        this.productService =
                productService;

        this.productRankingService =
                productRankingService;

        this.productEvaluationService =
                productEvaluationService;

        this.productSpecificationRepository =
                productSpecificationRepository;
    }


    // ============================================================
    // RECOMMEND BEST PRODUCT
    // ============================================================

    public RecommendationResponse recommendBestProduct(
            RecommendationRequest request) {

        request.validate();

        List<Product> products =
                productService.getAllProducts();


        // ========================================================
        // FILTER PRODUCTS
        // ========================================================

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


        // ========================================================
        // NO MATCHING PRODUCTS
        // ========================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your requirements. "
                    + "Try increasing your budget or "
                    + "changing your requirements."
            );
        }


        // ========================================================
        // RANK PRODUCTS
        // ========================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts)
                );


        // ========================================================
        // APPLY PREFERRED BRAND
        // ========================================================

        applyPreferredBrandPriority(
                rankedProducts,
                request.getPreferredBrand()
        );


        // ========================================================
        // SELECT BEST PRODUCT
        // ========================================================

        Product recommendedProduct =
                rankedProducts.get(0);


        // ========================================================
        // CALCULATE SCORE
        // ========================================================

        double score =
                productEvaluationService
                        .calculateScore(
                                recommendedProduct);


        // ========================================================
        // RECOMMENDATION REASON
        // ========================================================

        String reason =
                "Best product matching your category, "
                + "budget, and minimum rating preferences";


        // ========================================================
        // BUDGET EXPLANATION
        // ========================================================

        String budgetMessage =
                "Price ₹"
                + formatPrice(
                        recommendedProduct.getPrice())
                + " is within your maximum budget of ₹"
                + formatPrice(
                        request.getMaxPrice());


        // ========================================================
        // RATING EXPLANATION
        // ========================================================

        String ratingMessage =
                "Rating "
                + recommendedProduct.getRating()
                + " meets your minimum rating requirement of "
                + request.getMinRating();


        // ========================================================
        // PREFERENCE EXPLANATION
        // ========================================================

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


        // ========================================================
        // BRAND EXPLANATION
        // ========================================================

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


        // ========================================================
        // CREATE EXPLANATION
        // ========================================================

        RecommendationExplanation explanation =
                new RecommendationExplanation(
                        budgetMessage,
                        ratingMessage,
                        preferenceMessage,
                        brandMessage
                );


        // ========================================================
        // RETURN RESPONSE
        // ========================================================

        return new RecommendationResponse(
                recommendedProduct,
                score,
                reason,
                explanation
        );
    }


    // ============================================================
    // TOP-N RECOMMENDATIONS
    // ============================================================

    public RecommendationListResponse recommendTopProducts(
            RecommendationRequest request,
            int limit) {

        request.validate();


        // ========================================================
        // VALIDATE LIMIT
        // ========================================================

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


        // ========================================================
        // GET PRODUCTS
        // ========================================================

        List<Product> products =
                productService.getAllProducts();


        // ========================================================
        // FILTER PRODUCTS
        // ========================================================

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


        // ========================================================
        // NO MATCHING PRODUCTS
        // ========================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your requirements. "
                    + "Try increasing your budget or "
                    + "changing your requirements."
            );
        }


        // ========================================================
        // RANK PRODUCTS
        // ========================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts)
                );


        // ========================================================
        // APPLY PREFERRED BRAND PRIORITY
        // ========================================================

        applyPreferredBrandPriority(
                rankedProducts,
                request.getPreferredBrand()
        );


        // ========================================================
        // CREATE TOP-N RECOMMENDATIONS
        // ========================================================

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


        // ========================================================
        // RETURN TOP-N
        // ========================================================

        return new RecommendationListResponse(
                recommendations
        );
    }


    // ============================================================
    // PERSONALIZED RECOMMENDATION
    // ============================================================

    public Product recommendPersonalizedProduct(
            RecommendationRequest request) {

        request.validate();


        // ========================================================
        // GET PRODUCTS
        // ========================================================

        List<Product> products =
                productService.getAllProducts();


        // ========================================================
        // FILTER PRODUCTS
        // ========================================================

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


        // ========================================================
        // NO MATCHING PRODUCTS
        // ========================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your requirements. "
                    + "Try increasing your budget or "
                    + "changing your requirements."
            );
        }


        // ========================================================
        // PERSONALIZED RANKING
        // ========================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProductsByPreference(
                                        matchingProducts,
                                        request.getPriceWeight(),
                                        request.getRatingWeight()
                                )
                );


        // ========================================================
        // APPLY PREFERRED BRAND PRIORITY
        // ========================================================

        applyPreferredBrandPriority(
                rankedProducts,
                request.getPreferredBrand()
        );


        // ========================================================
        // RETURN BEST PRODUCT
        // ========================================================

        return rankedProducts.get(0);
    }


    // ============================================================
    // DAY 37
    // NATURAL LANGUAGE BEST PRODUCT
    // ============================================================

    public RecommendationResponse recommendBestProduct(
            ProductRequirement requirement) {

        List<Product> matchingProducts =
                filterByRequirements(
                        requirement);


        // ========================================================
        // NO MATCHING PRODUCTS
        // ========================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your natural-language "
                    + "requirements. Try relaxing one or more "
                    + "specifications."
            );
        }


        // ========================================================
        // RANK PRODUCTS
        // ========================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts)
                );


        // ========================================================
        // PREFERRED BRAND PRIORITY
        // ========================================================

        applyPreferredBrandPriority(
                rankedProducts,
                requirement.getPreferredBrand()
        );


        // ========================================================
        // SELECT BEST PRODUCT
        // ========================================================

        Product recommendedProduct =
                rankedProducts.get(0);


        // ========================================================
        // CALCULATE SCORE
        // ========================================================

        double score =
                productEvaluationService
                        .calculateScore(
                                recommendedProduct);


        // ========================================================
        // REASON
        // ========================================================

        String reason =
                buildNaturalLanguageReason(
                        requirement,
                        recommendedProduct);


        return new RecommendationResponse(
                recommendedProduct,
                score,
                reason
        );
    }


    // ============================================================
    // DAY 37
    // NATURAL LANGUAGE TOP-N
    // ============================================================

    public RecommendationListResponse recommendTopProducts(
            ProductRequirement requirement,
            int limit) {

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


        // ========================================================
        // FILTER USING NATURAL-LANGUAGE REQUIREMENTS
        // ========================================================

        List<Product> matchingProducts =
                filterByRequirements(
                        requirement);


        // ========================================================
        // NO MATCHING PRODUCTS
        // ========================================================

        if (matchingProducts.isEmpty()) {

            throw new RuntimeException(
                    "No products match your natural-language "
                    + "requirements. Try relaxing one or more "
                    + "specifications."
            );
        }


        // ========================================================
        // RANK
        // ========================================================

        List<Product> rankedProducts =
                new ArrayList<>(
                        productRankingService
                                .rankProducts(
                                        matchingProducts)
                );


        // ========================================================
        // PREFERRED BRAND
        // ========================================================

        applyPreferredBrandPriority(
                rankedProducts,
                requirement.getPreferredBrand()
        );


        // ========================================================
        // CREATE TOP-N
        // ========================================================

        List<RecommendationResponse> recommendations =
                rankedProducts.stream()

                        .limit(limit)

                        .map(product -> {

                            double score =
                                    productEvaluationService
                                            .calculateScore(
                                                    product);

                            String reason =
                                    buildNaturalLanguageReason(
                                            requirement,
                                            product);

                            return new RecommendationResponse(
                                    product,
                                    score,
                                    reason
                            );
                        })

                        .collect(Collectors.toList());


        return new RecommendationListResponse(
                recommendations
        );
    }


    // ============================================================
    // FILTER USING PRODUCT REQUIREMENTS
    // ============================================================

    private List<Product> filterByRequirements(
            ProductRequirement requirement) {

        if (requirement == null) {

            throw new IllegalArgumentException(
                    "Product requirements cannot be null"
            );
        }


        List<Product> products =
                productService.getAllProducts();


        return products.stream()

                // =================================================
                // CATEGORY
                // =================================================

                .filter(product -> {

                    if (requirement.getCategory() == null
                            || requirement.getCategory()
                                    .isBlank()) {

                        return true;
                    }

                    return product.getCategory()
                            .equalsIgnoreCase(
                                    requirement.getCategory());
                })


                // =================================================
                // AVAILABILITY
                // =================================================

                .filter(Product::isAvailable)


                // =================================================
                // MAXIMUM PRICE
                // =================================================

                .filter(product -> {

                    if (requirement.getMaxPrice() == null
                            || requirement.getMaxPrice()
                                    == Double.MAX_VALUE) {

                        return true;
                    }

                    return product.getPrice()
                            <= requirement.getMaxPrice();
                })


                // =================================================
                // MINIMUM RATING
                // =================================================

                .filter(product -> {

                    if (requirement.getMinRating() == null
                            || requirement.getMinRating()
                                    <= 0) {

                        return true;
                    }

                    return product.getRating()
                            >= requirement.getMinRating();
                })


                // =================================================
                // RAM
                // =================================================

                .filter(product ->
                        matchesNumericRequirement(
                                product,
                                "RAM",
                                requirement.getMinRam()
                        )
                )


                // =================================================
                // STORAGE
                // =================================================

                .filter(product ->
                        matchesNumericRequirement(
                                product,
                                "Storage",
                                requirement.getMinStorage()
                        )
                )


                // =================================================
                // CAMERA
                // =================================================

                .filter(product ->
                        matchesNumericRequirement(
                                product,
                                "Main Camera",
                                requirement.getMinCamera()
                        )
                )


                // =================================================
                // BATTERY
                // =================================================

                .filter(product ->
                        matchesNumericRequirement(
                                product,
                                "Battery",
                                requirement.getMinBattery()
                        )
                )


                // =================================================
                // DISPLAY SIZE
                // =================================================

                .filter(product ->
                        matchesNumericRequirement(
                                product,
                                "Display Size",
                                requirement.getMinDisplaySize()
                        )
                )


                // =================================================
                // REFRESH RATE
                // =================================================

                .filter(product ->
                        matchesNumericRequirement(
                                product,
                                "Refresh Rate",
                                requirement.getMinRefreshRate()
                        )
                )


                // =================================================
                // 5G
                // =================================================

                .filter(product ->
                        matchesBooleanRequirement(
                                product,
                                "5G",
                                requirement.getRequires5G()
                        )
                )


                .collect(Collectors.toList());
    }


    // ============================================================
    // NUMERIC SPECIFICATION MATCHING
    // ============================================================

    private boolean matchesNumericRequirement(
            Product product,
            String specificationName,
            Double minimumValue) {

        if (minimumValue == null) {
            return true;
        }


        List<ProductSpecification> specifications =
                productSpecificationRepository
                        .findByProductId(
                                product.getId());


        for (ProductSpecification specification :
                specifications) {

            if (specification
                    .getSpecificationName()
                    .equalsIgnoreCase(
                            specificationName)) {

                Double numericValue =
                        specification
                                .getNumericValue();

                if (numericValue == null) {
                    return false;
                }

                return numericValue
                        >= minimumValue;
            }
        }


        return false;
    }


    // ============================================================
    // BOOLEAN SPECIFICATION MATCHING
    // ============================================================

    private boolean matchesBooleanRequirement(
            Product product,
            String specificationName,
            Boolean requiredValue) {

        if (requiredValue == null) {
            return true;
        }


        List<ProductSpecification> specifications =
                productSpecificationRepository
                        .findByProductId(
                                product.getId());


        for (ProductSpecification specification :
                specifications) {

            if (specification
                    .getSpecificationName()
                    .equalsIgnoreCase(
                            specificationName)) {

                String value =
                        specification
                                .getSpecificationValue();

                if (value == null) {
                    return false;
                }

                boolean actualValue =
                        value.equalsIgnoreCase("yes")
                        || value.equalsIgnoreCase("true");

                return actualValue == requiredValue;
            }
        }


        return false;
    }


    // ============================================================
    // NATURAL LANGUAGE REASON
    // ============================================================

    private String buildNaturalLanguageReason(
            ProductRequirement requirement,
            Product product) {

        StringBuilder reason =
                new StringBuilder();

        reason.append(
                "Matches your natural-language requirements"
        );


        if (requirement.getMaxPrice() != null
                && requirement.getMaxPrice()
                        != Double.MAX_VALUE) {

            reason.append(
                    ", within your budget of ₹"
            );

            reason.append(
                    formatPrice(
                            requirement.getMaxPrice())
            );
        }


        if (requirement.getMinRam() != null) {

            reason.append(
                    ", meets the minimum RAM requirement"
            );
        }


        if (requirement.getMinStorage() != null) {

            reason.append(
                    ", meets the minimum storage requirement"
            );
        }


        if (requirement.getMinCamera() != null) {

            reason.append(
                    ", meets the minimum camera requirement"
            );
        }


        if (requirement.getMinBattery() != null) {

            reason.append(
                    ", meets the minimum battery requirement"
            );
        }


        if (requirement.getMinDisplaySize() != null) {

            reason.append(
                    ", meets the minimum display-size requirement"
            );
        }


        if (requirement.getMinRefreshRate() != null) {

            reason.append(
                    ", meets the minimum refresh-rate requirement"
            );
        }


        if (requirement.getRequires5G() != null
                && requirement.getRequires5G()) {

            reason.append(
                    ", supports 5G"
            );
        }


        if (requirement.getPreferredBrand() != null
                && !requirement.getPreferredBrand()
                        .isBlank()
                && product.getBrand()
                        .equalsIgnoreCase(
                                requirement.getPreferredBrand())) {

            reason.append(
                    ", matches your preferred brand"
            );
        }


        return reason.toString();
    }


    // ============================================================
    // PREFERRED BRAND PRIORITY
    // ============================================================

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


            if (product1Matches
                    && !product2Matches) {

                return -1;
            }


            if (!product1Matches
                    && product2Matches) {

                return 1;
            }


            return 0;
        });
    }


    // ============================================================
    // FORMAT PRICE
    // ============================================================

    private String formatPrice(
            double price) {

        return String.format(
                "%.0f",
                price
        );
    }
}
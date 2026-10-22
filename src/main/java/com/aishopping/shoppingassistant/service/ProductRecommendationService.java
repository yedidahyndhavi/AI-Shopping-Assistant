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
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {

    private final ProductService productService;
    private final ProductRankingService productRankingService;
    private final ProductEvaluationService productEvaluationService;
    private final ProductSpecificationRepository productSpecificationRepository;

    public ProductRecommendationService(
            ProductService productService,
            ProductRankingService productRankingService,
            ProductEvaluationService productEvaluationService,
            ProductSpecificationRepository productSpecificationRepository) {

        this.productService = productService;
        this.productRankingService = productRankingService;
        this.productEvaluationService = productEvaluationService;
        this.productSpecificationRepository = productSpecificationRepository;
    }

    // ============================================================
    // RECOMMEND BEST PRODUCT - STRUCTURED REQUEST
    // ============================================================

    public RecommendationResponse recommendBestProduct(
            RecommendationRequest request) {

        request.validate();

        List<Product> matchingProducts = productService.getAllProducts()
                .stream()
                .filter(product -> product.getCategory() != null
                        && product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .filter(Product::isAvailable)
                .filter(product -> product.getPrice() <= request.getMaxPrice())
                .filter(product -> product.getRating() >= request.getMinRating())
                .collect(Collectors.toList());

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException(
                    "No products match your requirements. "
                            + "Try increasing your budget or changing your requirements.");
        }

        List<Product> rankedProducts = new ArrayList<>(
                productRankingService.rankProducts(matchingProducts));

        applyPreferredBrandPriority(
                rankedProducts, request.getPreferredBrand());

        Product product = rankedProducts.get(0);
        double score = productEvaluationService.calculateScore(product);

        String reason = "Best product matching your category, budget, "
                + "and minimum rating preferences";

        String budgetMessage = "Price ₹" + formatPrice(product.getPrice())
                + " is within your maximum budget of ₹"
                + formatPrice(request.getMaxPrice());

        String ratingMessage = "Rating " + product.getRating()
                + " meets your minimum rating requirement of "
                + request.getMinRating();

        String preferenceMessage;

        if (request.getPriceWeight() > request.getRatingWeight()) {
            preferenceMessage =
                    "Recommendation gives higher importance to your price preference";
        } else if (request.getRatingWeight() > request.getPriceWeight()) {
            preferenceMessage =
                    "Recommendation gives higher importance to your rating preference";
        } else {
            preferenceMessage =
                    "Recommendation gives equal importance to price and rating";
        }

        String brandMessage = "No specific brand preference was provided";

        if (request.getPreferredBrand() != null
                && !request.getPreferredBrand().isBlank()) {

            if (product.getBrand() != null
                    && product.getBrand().equalsIgnoreCase(
                            request.getPreferredBrand())) {

                brandMessage = "The recommended product matches your preferred brand: "
                        + request.getPreferredBrand();
            } else {
                brandMessage = "No product from your preferred brand "
                        + "was selected as the top recommendation";
            }
        }
        System.out.println("DEBUG ranked brands = "
        + rankedProducts.stream()
                .map(Product::getBrand)
                .toList());

        RecommendationExplanation explanation = new RecommendationExplanation(
                budgetMessage,
                ratingMessage,
                preferenceMessage,
                brandMessage
        );

        return new RecommendationResponse(product, score, reason, explanation);
    }

    // ============================================================
    // TOP-N RECOMMENDATIONS - STRUCTURED REQUEST
    // ============================================================

    public RecommendationListResponse recommendTopProducts(
            RecommendationRequest request, int limit) {

        request.validate();
        validateLimit(limit);

        List<Product> matchingProducts = productService.getAllProducts()
                .stream()
                .filter(product -> product.getCategory() != null
                        && product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .filter(Product::isAvailable)
                .filter(product -> product.getPrice() <= request.getMaxPrice())
                .filter(product -> product.getRating() >= request.getMinRating())
                .collect(Collectors.toList());

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException("No products match your requirements.");
        }

        List<Product> rankedProducts = new ArrayList<>(
                productRankingService.rankProducts(matchingProducts));

        applyPreferredBrandPriority(
                rankedProducts, request.getPreferredBrand());

        List<RecommendationResponse> recommendations = rankedProducts.stream()
                .limit(limit)
                .map(product -> new RecommendationResponse(
                        product,
                        productEvaluationService.calculateScore(product),
                        "Matches your category, budget, and rating preferences"
                ))
                .collect(Collectors.toList());

        return new RecommendationListResponse(recommendations);
    }

    // ============================================================
    // PERSONALIZED RECOMMENDATION
    // ============================================================

    public Product recommendPersonalizedProduct(
            RecommendationRequest request) {

        request.validate();

        List<Product> matchingProducts = productService.getAllProducts()
                .stream()
                .filter(product -> product.getCategory() != null
                        && product.getCategory().equalsIgnoreCase(
                                request.getCategory()))
                .filter(Product::isAvailable)
                .filter(product -> product.getPrice() <= request.getMaxPrice())
                .filter(product -> product.getRating() >= request.getMinRating())
                .collect(Collectors.toList());

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException("No products match your requirements.");
        }

        List<Product> rankedProducts = new ArrayList<>(
                productRankingService.rankProductsByPreference(
                        matchingProducts,
                        request.getPriceWeight(),
                        request.getRatingWeight()
                ));

        applyPreferredBrandPriority(
                rankedProducts, request.getPreferredBrand());

        return rankedProducts.get(0);
    }

    // ============================================================
    // NATURAL-LANGUAGE BEST PRODUCT
    // ============================================================

    public RecommendationResponse recommendBestProduct(
            ProductRequirement requirement) {

        List<Product> matchingProducts = filterByRequirements(requirement);

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException(
                    "No products match your natural-language requirements. "
                            + "Try relaxing one or more specifications.");
        }

        List<Product> rankedProducts = new ArrayList<>(
                productRankingService.rankProducts(matchingProducts));

        applyPreferredBrandPriority(
                rankedProducts, requirement.getPreferredBrand());

        Product product = rankedProducts.get(0);

        String reason = buildNaturalLanguageReason(requirement, product);

        RecommendationExplanation explanation =
                buildRecommendationExplanation(requirement, product);

        return new RecommendationResponse(
                product,
                productEvaluationService.calculateScore(product),
                reason,
                explanation
        );
    }

    // ============================================================
    // NATURAL-LANGUAGE TOP-N
    // ============================================================

    public RecommendationListResponse recommendTopProducts(
            ProductRequirement requirement, int limit) {

        validateLimit(limit);

        List<Product> matchingProducts = filterByRequirements(requirement);

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException(
                    "No products match your natural-language requirements. "
                            + "Try relaxing one or more specifications.");
        }

        List<Product> rankedProducts = new ArrayList<>(
                productRankingService.rankProducts(matchingProducts));

        applyPreferredBrandPriority(
                rankedProducts, requirement.getPreferredBrand());

        List<RecommendationResponse> recommendations = rankedProducts.stream()
                .limit(limit)
                .map(product -> new RecommendationResponse(
                        product,
                        productEvaluationService.calculateScore(product),
                        buildNaturalLanguageReason(requirement, product),
                        buildRecommendationExplanation(requirement, product)
                ))
                .collect(Collectors.toList());

        return new RecommendationListResponse(recommendations);
    }

    // ============================================================
    // FILTER PRODUCTS USING ALL REQUIREMENTS
    // ============================================================

    
private List<Product> filterByRequirements(
        ProductRequirement requirement) {

    if (requirement == null) {
        throw new IllegalArgumentException(
                "Product requirements cannot be null");
    }

    return productService.getAllProducts()
            .stream()

            // Category
            .filter(product -> {
                String requested = requirement.getCategory();

                return requested == null || requested.isBlank()
                        || (product.getCategory() != null
                        && product.getCategory()
                        .equalsIgnoreCase(requested));
            })

            // Brand: strictly filter when the requested brand exists
            .filter(product -> {
                String brand = requirement.getPreferredBrand();

                return brand == null || brand.isBlank()
                        || (product.getBrand() != null
                        && product.getBrand()
                        .trim()
                        .equalsIgnoreCase(brand.trim()));
            })

            // Availability
            .filter(Product::isAvailable)

            // Maximum price
            .filter(product -> requirement.getMaxPrice() == null
                    || requirement.getMaxPrice() == Double.MAX_VALUE
                    || product.getPrice() <= requirement.getMaxPrice())

            // Minimum rating, including strict "above" handling
            .filter(product -> {
                Double rating = requirement.getMinRating();

                if (rating == null || rating <= 0) {
                    return true;
                }

                if (requirement.isStrictMinRating()) {
                    return product.getRating() > rating;
                }

                return product.getRating() >= rating;
            })

            // Smartphone specifications
            .filter(product -> matchesNumericRequirement(
                    product, "RAM", requirement.getMinRam()))
            .filter(product -> matchesNumericRequirement(
                    product, "Storage", requirement.getMinStorage()))
            .filter(product -> matchesNumericRequirement(
                    product, "Main Camera", requirement.getMinCamera()))
            .filter(product -> matchesNumericRequirement(
                    product, "Battery", requirement.getMinBattery()))
            .filter(product -> matchesNumericAliases(
                    product,
                    new String[]{"Display Size", "Screen Size"},
                    requirement.getMinDisplaySize()))
            .filter(product -> matchesNumericRequirement(
                    product, "Refresh Rate",
                    requirement.getMinRefreshRate()))
            .filter(product -> matchesBooleanRequirement(
                    product, "5G", requirement.getRequires5G()))

            // Laptop processor
            .filter(product -> matchesTextRequirement(
                    product, "Processor",
                    requirement.getPreferredProcessor()))

            // Smart TV specifications
            .filter(product -> matchesResolution(
                    product, requirement.getRequiredResolution()))
            .filter(product -> matchesTextRequirement(
                    product, "Display Technology",
                    requirement.getPreferredDisplayTechnology()))

            // Headphones
            .filter(product -> matchesNoiseCancellation(
                    product, requirement.getRequiresNoiseCancellation()))
            .filter(product -> matchesBatteryLife(
                    product, requirement.getMinBatteryLife()))
            .filter(product -> matchesMaximumWeight(
                    product, requirement.getMaxWeight()))

            // Smartwatch
            .filter(product -> matchesWaterResistance(
                    product, requirement.getRequiredWaterResistance()))
            .filter(product -> matchesMinimumWaterResistance(
                    product, requirement.getMinWaterResistance()))
            .filter(product -> matchesCompatibility(
                    product, requirement.getPreferredCompatibility()))

            .collect(Collectors.toList());
}


    // ============================================================
    // SPECIFICATION LOOKUPS
    // ============================================================

    private List<ProductSpecification> getSpecifications(Product product) {
        return productSpecificationRepository.findByProductId(product.getId());
    }

    private Double getNumericValue(
            Product product, String... names) {

        for (ProductSpecification specification : getSpecifications(product)) {

            if (specification.getSpecificationName() == null) {
                continue;
            }

            for (String name : names) {
                if (specification.getSpecificationName()
                        .equalsIgnoreCase(name)) {
                    return specification.getNumericValue();
                }
            }
        }

        return null;
    }

    private String getTextValue(Product product, String... names) {

        for (ProductSpecification specification : getSpecifications(product)) {

            if (specification.getSpecificationName() == null) {
                continue;
            }

            for (String name : names) {
                if (specification.getSpecificationName()
                        .equalsIgnoreCase(name)) {
                    return specification.getSpecificationValue();
                }
            }
        }

        return null;
    }

    private boolean matchesNumericRequirement(
            Product product, String name, Double minimum) {

        if (minimum == null) {
            return true;
        }

        Double value = getNumericValue(product, name);

        return value != null && value >= minimum;
    }

    private boolean matchesNumericAliases(
            Product product, String[] names, Double minimum) {

        if (minimum == null) {
            return true;
        }

        Double value = getNumericValue(product, names);

        return value != null && value >= minimum;
    }

    private boolean matchesBooleanRequirement(
            Product product, String name, Boolean required) {

        if (required == null) {
            return true;
        }

        String value = getTextValue(product, name);

        if (value == null) {
            return false;
        }

        boolean actual = value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("supported");

        return actual == required;
    }

    private boolean matchesTextRequirement(
            Product product, String name, String required) {

        if (required == null || required.isBlank()) {
            return true;
        }

        String actual = getTextValue(product, name);

        if (actual == null) {
            return false;
        }

        return actual.toLowerCase(Locale.ROOT)
                .contains(required.toLowerCase(Locale.ROOT));
    }

    // ============================================================
    // TV RESOLUTION
    // ============================================================

    private boolean matchesResolution(
            Product product, String required) {

        if (required == null || required.isBlank()) {
            return true;
        }

        String actual = getTextValue(product, "Resolution");

        if (actual == null) {
            return false;
        }

        int requiredLevel = resolutionLevel(required);
        int actualLevel = resolutionLevel(actual);

        return requiredLevel > 0 && actualLevel >= requiredLevel;
    }

    private int resolutionLevel(String resolution) {

        String value = resolution.toLowerCase(Locale.ROOT)
                .replace(" ", "");

        if (value.contains("8k") || value.contains("4320")) {
            return 4;
        }

        if (value.contains("4k") || value.contains("2160")
                || value.contains("uhd")) {
            return 3;
        }

        if (value.contains("1080") || value.contains("fullhd")
                || value.equals("fhd")) {
            return 2;
        }

        if (value.contains("720") || value.equals("hd")) {
            return 1;
        }

        return 0;
    }

    // ============================================================
    // HEADPHONE NOISE CANCELLATION
    // ============================================================

    private boolean matchesNoiseCancellation(
            Product product, Boolean required) {

        if (required == null) {
            return true;
        }

        String value = getTextValue(
                product, "Noise Cancellation");

        if (value == null) {
            return false;
        }

        String normalized = value.toLowerCase(Locale.ROOT);

        boolean actual = normalized.equals("yes")
                || normalized.equals("true")
                || normalized.contains("active")
                || normalized.contains("anc");

        return actual == required;
    }

    // ============================================================
    // BATTERY LIFE - NORMALIZE TO HOURS
    // ============================================================

    private boolean matchesBatteryLife(
            Product product, Double minimumHours) {

        if (minimumHours == null) {
            return true;
        }

        Double value = getNumericValue(product, "Battery Life");

        if (value == null) {
            return false;
        }

        String unit = getSpecificationUnit(product, "Battery Life");

        if (unit != null) {
            String normalized = unit.toLowerCase(Locale.ROOT);

            if (normalized.startsWith("day")) {
                value *= 24;
            } else if (normalized.startsWith("min")) {
                value /= 60;
            }
        }

        return value >= minimumHours;
    }

    private String getSpecificationUnit(Product product, String name) {

        for (ProductSpecification specification : getSpecifications(product)) {

            if (specification.getSpecificationName() != null
                    && specification.getSpecificationName()
                    .equalsIgnoreCase(name)) {
                return specification.getUnit();
            }
        }

        return null;
    }

    // ============================================================
    // HEADPHONE WEIGHT - NORMALIZE TO GRAMS
    // ============================================================

    private boolean matchesMaximumWeight(
            Product product, Double maximumGrams) {

        if (maximumGrams == null) {
            return true;
        }

        Double weight = getNumericValue(product, "Weight");

        if (weight == null) {
            return false;
        }

        String unit = getSpecificationUnit(product, "Weight");

        if (unit != null) {
            String normalized = unit.toLowerCase(Locale.ROOT);

            if (normalized.equals("kg")
                    || normalized.startsWith("kilogram")) {
                weight *= 1000;
            }
        }

        return weight <= maximumGrams;
    }

    // ============================================================
    // SMARTWATCH WATER RESISTANCE
    // ============================================================

    private boolean matchesWaterResistance(
            Product product, String required) {

        if (required == null || required.isBlank()
                || required.equalsIgnoreCase("Water Resistant")) {

            if (required == null || required.isBlank()) {
                return true;
            }

            String actual = getTextValue(product, "Water Resistance");

            return actual != null
                    && !actual.equalsIgnoreCase("No")
                    && !actual.equalsIgnoreCase("None");
        }

        String actual = getTextValue(product, "Water Resistance");

        if (actual == null) {
            return false;
        }

        String normalizedActual = actual.replace(" ", "")
                .toLowerCase(Locale.ROOT);

        String normalizedRequired = required.replace(" ", "")
                .toLowerCase(Locale.ROOT);

        if (normalizedRequired.contains("atm")) {

            Double actualAtm = extractAtm(normalizedActual);
            Double requiredAtm = extractAtm(normalizedRequired);

            return actualAtm != null && requiredAtm != null
                    && actualAtm >= requiredAtm;
        }

        return normalizedActual.contains(normalizedRequired);
    }

    private boolean matchesMinimumWaterResistance(
            Product product, Double minimumAtm) {

        if (minimumAtm == null) {
            return true;
        }

        String actual = getTextValue(product, "Water Resistance");

        if (actual == null) {
            return false;
        }

        Double actualAtm = extractAtm(
                actual.replace(" ", "").toLowerCase(Locale.ROOT));

        return actualAtm != null && actualAtm >= minimumAtm;
    }

    private Double extractAtm(String value) {

        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(\\d+(?:\\.\\d+)?)atm")
                .matcher(value);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return null;
    }

    // ============================================================
    // SMARTWATCH COMPATIBILITY
    // ============================================================

    private boolean matchesCompatibility(
            Product product, String required) {

        if (required == null || required.isBlank()) {
            return true;
        }

        String actual = getTextValue(product, "Compatibility");

        if (actual == null) {
            return false;
        }

        String normalizedActual = actual.toLowerCase(Locale.ROOT);
        String normalizedRequired = required.toLowerCase(Locale.ROOT);

        if (normalizedRequired.equals("android/ios")) {
            return normalizedActual.contains("android")
                    && normalizedActual.contains("ios");
        }

        return normalizedActual.contains(normalizedRequired);
    }

    // ============================================================
    // NATURAL-LANGUAGE EXPLANATION
    // ============================================================

    private String buildNaturalLanguageReason(
            ProductRequirement requirement, Product product) {

        StringBuilder reason = new StringBuilder(
                "Matches your natural-language requirements");

        if (requirement.getMaxPrice() != null
                && requirement.getMaxPrice() != Double.MAX_VALUE) {
            reason.append(", within your budget of ₹")
                    .append(formatPrice(requirement.getMaxPrice()));
        }

        if (requirement.getMinRam() != null) {
            reason.append(", meets your minimum RAM requirement");
        }

        if (requirement.getMinStorage() != null) {
            reason.append(", meets your minimum storage requirement");
        }

        if (requirement.getMinCamera() != null) {
            reason.append(", meets your minimum camera requirement");
        }

        if (requirement.getMinBattery() != null) {
            reason.append(", meets your minimum battery requirement");
        }

        if (requirement.getMinDisplaySize() != null) {
            reason.append(", meets your minimum display-size requirement");
        }

        if (requirement.getMinRefreshRate() != null) {
            reason.append(", meets your minimum refresh-rate requirement");
        }

        if (Boolean.TRUE.equals(requirement.getRequires5G())) {
            reason.append(", supports 5G");
        }

        if (requirement.getRequiredResolution() != null) {
            reason.append(", matches your requested resolution");
        }

        if (Boolean.TRUE.equals(
                requirement.getRequiresNoiseCancellation())) {
            reason.append(", supports noise cancellation");
        }

        if (requirement.getMinBatteryLife() != null) {
            reason.append(", meets your battery-life requirement");
        }

        if (requirement.getRequiredWaterResistance() != null
                || requirement.getMinWaterResistance() != null) {
            reason.append(", meets your water-resistance requirement");
        }

        if (requirement.getPreferredBrand() != null
                && product.getBrand() != null
                && product.getBrand().equalsIgnoreCase(
                        requirement.getPreferredBrand())) {
            reason.append(", matches your preferred brand");
        }

        return reason.toString();
    }

    // ============================================================
    // DAY 42 - RECOMMENDATION EXPLANATION BUILDER
    // ============================================================

    private RecommendationExplanation buildRecommendationExplanation(
            ProductRequirement requirement,
            Product product) {

        RecommendationExplanation explanation =
                new RecommendationExplanation();

        // Budget explanation
        if (requirement.getMaxPrice() != null
                && requirement.getMaxPrice() != Double.MAX_VALUE) {

            explanation.setBudgetMessage(
                    "Price ₹" + formatPrice(product.getPrice())
                    + " is within your budget of ₹"
                    + formatPrice(requirement.getMaxPrice()));
        } else {
            explanation.setBudgetMessage(
                    "No maximum budget was specified.");
        }

        // Rating explanation
        
        // Rating explanation
        if (requirement.getMinRating() != null
                && requirement.getMinRating() > 0) {

            String ratingCondition =
                    requirement.isStrictMinRating()
                            ? "must be greater than "
                            : "must be at least ";

            explanation.setRatingMessage(
                    "Product rating: " + product.getRating()
                    + ". Your requirement: rating "
                    + ratingCondition
                    + requirement.getMinRating()
                    + "."
            );

        } else {
            explanation.setRatingMessage(
                    "No minimum rating was specified."
            );
        }

        // Brand explanation
        if (requirement.getPreferredBrand() != null
                && !requirement.getPreferredBrand().isBlank()) {

            if (product.getBrand() != null
                    && product.getBrand().equalsIgnoreCase(
                            requirement.getPreferredBrand())) {

                explanation.setBrandMessage(
                        "Matches your preferred brand: "
                        + requirement.getPreferredBrand());
            } else {
                explanation.setBrandMessage(
                        "The selected product does not match "
                        + "your preferred brand.");
            }
        } else {
            explanation.setBrandMessage(
                    "No specific brand preference was provided.");
        }

        // Matched requirements
        List<String> matched = new ArrayList<>();
        List<String> unmatched = new ArrayList<>();

        if (requirement.getCategory() != null
                && !requirement.getCategory().isBlank()) {
            matched.add("Category: " + product.getCategory());
        }

        if (requirement.getMaxPrice() != null
                && requirement.getMaxPrice() != Double.MAX_VALUE) {
            matched.add("Within the requested budget");
        }

        if (requirement.getMinRating() != null
                && requirement.getMinRating() > 0) {
            matched.add("Meets the minimum rating requirement");
        }

        if (requirement.getMinRam() != null) {
            matched.add("Meets the minimum RAM requirement");
        }

        if (requirement.getMinStorage() != null) {
            matched.add("Meets the minimum storage requirement");
        }

        if (requirement.getMinCamera() != null) {
            matched.add("Meets the minimum camera requirement");
        }

        if (requirement.getMinBattery() != null) {
            matched.add("Meets the minimum battery capacity requirement");
        }

        if (requirement.getMinDisplaySize() != null) {
            matched.add("Meets the minimum display-size requirement");
        }

        if (requirement.getMinRefreshRate() != null) {
            matched.add("Meets the minimum refresh-rate requirement");
        }

        if (Boolean.TRUE.equals(requirement.getRequires5G())) {
            matched.add("Supports 5G");
        }

        if (requirement.getPreferredProcessor() != null) {
            matched.add("Matches the requested processor");
        }

        if (requirement.getRequiredResolution() != null) {
            matched.add("Meets the requested resolution");
        }

        if (requirement.getPreferredDisplayTechnology() != null) {
            matched.add("Matches the requested display technology");
        }

        if (Boolean.TRUE.equals(
                requirement.getRequiresNoiseCancellation())) {
            matched.add("Supports noise cancellation");
        }

        if (requirement.getMinBatteryLife() != null) {
            matched.add("Meets the minimum battery-life requirement");
        }

        if (requirement.getMaxWeight() != null) {
            matched.add("Meets the maximum weight requirement");
        }

        if (requirement.getRequiredWaterResistance() != null
                || requirement.getMinWaterResistance() != null) {
            matched.add("Meets the requested water-resistance requirement");
        }

        if (requirement.getPreferredCompatibility() != null) {
            matched.add("Matches the requested compatibility");
        }

        if (requirement.getPreferredBrand() != null
                && product.getBrand() != null
                && product.getBrand().equalsIgnoreCase(
                        requirement.getPreferredBrand())) {
            matched.add("Matches the preferred brand");
        }

        explanation.setMatchedRequirements(matched);

        // Existing filters remove products that fail the requested criteria.
        // This version does not calculate individual failed-spec diagnostics.
        explanation.setUnmatchedRequirements(unmatched);

        explanation.setRecommendationReason(
                buildNaturalLanguageReason(requirement, product));

        explanation.setPreferenceMessage(
                "The recommendation satisfies the filters "
                + "extracted from your shopping query.");

        return explanation;
    }

    // ============================================================
    // PREFERRED BRAND PRIORITY
    // ============================================================

    
private void applyPreferredBrandPriority(
        List<Product> products, String preferredBrand) {

    if (products == null || products.isEmpty()
            || preferredBrand == null || preferredBrand.isBlank()) {
        return;
    }

    products.sort((product1, product2) -> {
        boolean firstMatches = product1.getBrand() != null
                && product1.getBrand().trim()
                        .equalsIgnoreCase(preferredBrand.trim());

        boolean secondMatches = product2.getBrand() != null
                && product2.getBrand().trim()
                        .equalsIgnoreCase(preferredBrand.trim());

        if (firstMatches && !secondMatches) {
            return -1;
        }

        if (!firstMatches && secondMatches) {
            return 1;
        }

        return 0;
    });
}


    private void validateLimit(int limit) {

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Recommendation limit must be greater than 0");
        }

        if (limit > 10) {
            throw new IllegalArgumentException(
                    "Recommendation limit cannot exceed 10");
        }
    }

    private String formatPrice(double price) {
        return String.format(Locale.ROOT, "%.0f", price);
    }
}
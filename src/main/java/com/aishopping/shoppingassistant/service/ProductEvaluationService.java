
package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductEvaluationService {

    private final ProductSpecificationRepository productSpecificationRepository;

    public ProductEvaluationService(
            ProductSpecificationRepository productSpecificationRepository) {
        this.productSpecificationRepository = productSpecificationRepository;
    }

    /*
     * Main scoring dispatcher.
     * All category scores are normalized to a 0-100 scale.
     */
    public double calculateScore(Product product) {
        if (product == null || product.getCategory() == null) {
            return product == null ? 0 : calculateBasicScore(product);
        }

        String category = product.getCategory().toLowerCase(Locale.ROOT);

        if (category.contains("smartphone") || category.contains("smart phone")) {
            return calculateSmartphoneScore(product);
        }

        if (category.contains("laptop") || category.contains("notebook")) {
            return calculateLaptopScore(product);
        }

        if (category.contains("tv") || category.contains("television")) {
            return calculateTvScore(product);
        }

        if (category.contains("headphone")
                || category.contains("earphone")
                || category.contains("earbud")
                || category.contains("headset")) {
            return calculateHeadphonesScore(product);
        }

        if (category.contains("smartwatch")
                || category.contains("smart watch")
                || category.contains("fitness tracker")) {
            return calculateSmartwatchScore(product);
        }

        return calculateBasicScore(product);
    }

    /*
     * SMARTPHONE SCORING
     *
     * Rating       20%
     * Price        20%
     * RAM          10%
     * Storage      10%
     * Main Camera  10%
     * Battery      10%
     * Display       5%
     * Refresh Rate 10%
     * 5G             5%
     */
    private double calculateSmartphoneScore(Product product) {
        double ratingScore = calculateRatingScore(product);
        double priceScore = calculatePriceScore(product);

        double ramScore = calculateSpecificationScore(
                product, "RAM", 16);

        double storageScore = calculateSpecificationScore(
                product, "Storage", 512);

        double cameraScore = calculateSpecificationScore(
                product, "Main Camera", 200);

        double batteryScore = calculateSpecificationScore(
                product, "Battery", 6000);

        double displayScore = calculateSpecificationScore(
                product, "Display Size", 7.0);

        double refreshRateScore = calculateSpecificationScore(
                product, "Refresh Rate", 120);

        double fiveGScore = calculateBooleanSpecificationScore(
                product, "5G");

        return ratingScore * 0.20
                + priceScore * 0.20
                + ramScore * 0.10
                + storageScore * 0.10
                + cameraScore * 0.10
                + batteryScore * 0.10
                + displayScore * 0.05
                + refreshRateScore * 0.10
                + fiveGScore * 0.05;
    }

    /*
     * LAPTOP SCORING
     *
     * Rating       15%
     * Price        20%
     * Processor    10%
     * RAM          15%
     * Storage      10%
     * GPU          10%
     * Display       5%
     * Refresh Rate  5%
     * Battery       5%
     * Weight        5%
     */
    private double calculateLaptopScore(Product product) {
        double ratingScore = calculateRatingScore(product);
        double priceScore = calculatePriceScore(product);
        double processorScore = calculateProcessorScore(product);

        double ramScore = calculateSpecificationScore(
                product, "RAM", 32);

        double storageScore = calculateSpecificationScore(
                product, "Storage", 1024);

        double gpuScore = calculateGpuScore(product);

        double displayScore = calculateSpecificationScore(
                product, "Display Size", 17.3);

        double refreshRateScore = calculateSpecificationScore(
                product, "Refresh Rate", 240);

        double batteryScore = calculateSpecificationScore(
                product, "Battery", 100);

        double weightScore = calculateWeightScore(product);

        return ratingScore * 0.15
                + priceScore * 0.20
                + processorScore * 0.10
                + ramScore * 0.15
                + storageScore * 0.10
                + gpuScore * 0.10
                + displayScore * 0.05
                + refreshRateScore * 0.05
                + batteryScore * 0.05
                + weightScore * 0.05;
    }

    /*
     * SMART TV SCORING
     *
     * Rating       25%
     * Price        20%
     * Screen Size  20%
     * Resolution   20%
     * Refresh Rate 15%
     */
    private double calculateTvScore(Product product) {
        double ratingScore = calculateRatingScore(product);
        double priceScore = calculatePriceScore(product);

        double screenSizeScore = calculateSpecificationScore(
                product, "Screen Size", 85);

        double refreshRateScore = calculateSpecificationScore(
                product, "Refresh Rate", 240);

        double resolutionScore = calculateResolutionScore(product);

        return ratingScore * 0.25
                + priceScore * 0.20
                + screenSizeScore * 0.20
                + resolutionScore * 0.20
                + refreshRateScore * 0.15;
    }

    /*
     * Resolution scoring.
     * Checks 8K and 4K before generic HD keywords.
     */
    private double calculateResolutionScore(Product product) {
        String resolution = getSpecificationValue(product, "Resolution");

        if (resolution == null || resolution.isBlank()) {
            return 0;
        }

        String value = resolution.toLowerCase(Locale.ROOT)
                .replace(" ", "");

        if (value.contains("8k") || value.contains("4320")) {
            return 100;
        }

        if (value.contains("4k") || value.contains("2160")) {
            return 85;
        }

        if (value.contains("1080") || value.contains("fullhd")) {
            return 60;
        }

        if (value.contains("720") || value.equals("hd")) {
            return 35;
        }

        return 0;
    }

    /*
     * HEADPHONES SCORING
     *
     * Rating            30%
     * Price             20%
     * Battery Life      20%
     * Noise Cancellation 20%
     * Weight            10%
     *
     * Weight is assumed to be stored in grams.
     */
    private double calculateHeadphonesScore(Product product) {
        double ratingScore = calculateRatingScore(product);
        double priceScore = calculatePriceScore(product);

        double batteryScore = calculateSpecificationScore(
                product, "Battery Life", 60);

        double noiseCancellationScore =
                calculateNoiseCancellationScore(product);

        double weightScore = calculateHeadphoneWeightScore(product);

        return ratingScore * 0.30
                + priceScore * 0.20
                + batteryScore * 0.20
                + noiseCancellationScore * 0.20
                + weightScore * 0.10;
    }

    private double calculateNoiseCancellationScore(Product product) {
        String value = getSpecificationValue(
                product, "Noise Cancellation");

        if (value == null || value.isBlank()) {
            return 0;
        }

        String normalized = value.toLowerCase(Locale.ROOT);

        if (normalized.contains("no")
                || normalized.contains("none")
                || normalized.equals("false")) {
            return 0;
        }

        if (normalized.contains("yes")
                || normalized.contains("active")
                || normalized.equals("true")) {
            return 100;
        }

        return 0;
    }

    private double calculateHeadphoneWeightScore(Product product) {
        Double weight = getSpecificationNumericValue(product, "Weight");

        if (weight == null || weight <= 0) {
            return 0;
        }

        // Lighter headphones receive a higher score.
        return clampScore(100 - (weight / 5.0));
    }

    /*
     * SMARTWATCH SCORING
     *
     * Rating         30%
     * Price          20%
     * Battery Life   20%
     * Display Size   10%
     * Water Resistance 20%
     */
    private double calculateSmartwatchScore(Product product) {
        double ratingScore = calculateRatingScore(product);
        double priceScore = calculatePriceScore(product);

        double batteryScore = calculateSpecificationScore(
                product, "Battery Life", 14);

        double displayScore = calculateSpecificationScore(
                product, "Display Size", 2.5);

        double waterResistanceScore =
                calculateWaterResistanceScore(product);

        return ratingScore * 0.30
                + priceScore * 0.20
                + batteryScore * 0.20
                + displayScore * 0.10
                + waterResistanceScore * 0.20;
    }

    private double calculateWaterResistanceScore(Product product) {
        String value = getSpecificationValue(
                product, "Water Resistance");

        if (value == null || value.isBlank()) {
            return 0;
        }

        String normalized = value.toLowerCase(Locale.ROOT)
                .replace(" ", "");

        if (normalized.contains("10atm")) {
            return 100;
        }

        if (normalized.contains("5atm")) {
            return 85;
        }

        if (normalized.contains("3atm")
                || normalized.contains("ip68")) {
            return 65;
        }

        if (normalized.contains("ip67")) {
            return 50;
        }

        if (normalized.equals("no")
                || normalized.equals("none")) {
            return 0;
        }

        return 0;
    }

    /*
     * BASIC SCORING
     * Used for categories without dedicated scoring rules.
     */
    private double calculateBasicScore(Product product) {
        return calculateRatingScore(product) * 0.60
                + calculatePriceScore(product) * 0.40;
    }

    /*
     * Rating score: rating out of 5 converted to 0-100.
     */
    private double calculateRatingScore(Product product) {
        double rating = product.getRating();

        return clampScore((rating / 5.0) * 100);
    }

    /*
     * Lower price receives a higher score.
     * The reference price is ₹100,000.
     */
    private double calculatePriceScore(Product product) {
        double price = product.getPrice();

        if (price <= 0) {
            return 0;
        }

        return clampScore(100 - (price / 100000.0) * 100);
    }

    /*
     * Generic numeric specification scoring.
     *
     * Example: 12 GB RAM / 16 GB reference = 75 points.
     */
    private double calculateSpecificationScore(
            Product product,
            String specificationName,
            double maximumValue) {

        Double value = getSpecificationNumericValue(
                product, specificationName);

        if (value == null || maximumValue <= 0 || value <= 0) {
            return 0;
        }

        return clampScore((value / maximumValue) * 100);
    }

    /*
     * Boolean specification scoring, such as 5G support.
     */
    private double calculateBooleanSpecificationScore(
            Product product,
            String specificationName) {

        String value = getSpecificationValue(
                product, specificationName);

        if (value == null) {
            return 0;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        if (normalized.equals("yes")
                || normalized.equals("true")
                || normalized.equals("supported")) {
            return 100;
        }

        return 0;
    }

    /*
     * Laptop processor scoring.
     */
    private double calculateProcessorScore(Product product) {
        String processor = getSpecificationValue(product, "Processor");

        if (processor == null) {
            return 0;
        }

        String value = processor.toLowerCase(Locale.ROOT);

        if (value.contains("m4") || value.contains("i9")) {
            return 100;
        }

        if (value.contains("i7")) {
            return 90;
        }

        if (value.contains("i5")) {
            return 75;
        }

        if (value.contains("i3")) {
            return 55;
        }

        return 50;
    }

    /*
     * Laptop GPU scoring.
     */
    private double calculateGpuScore(Product product) {
        String gpu = getSpecificationValue(product, "GPU");

        if (gpu == null) {
            return 0;
        }

        String value = gpu.toLowerCase(Locale.ROOT);

        if (value.contains("rtx 5090")) {
            return 100;
        }

        if (value.contains("rtx 4090")) {
            return 100;
        }

        if (value.contains("rtx 5080")
                || value.contains("rtx 4080")) {
            return 95;
        }

        if (value.contains("rtx 5070")
                || value.contains("rtx 4070")) {
            return 90;
        }

        if (value.contains("rtx 5060")
                || value.contains("rtx 4060")) {
            return 85;
        }

        if (value.contains("rtx 5050")
                || value.contains("rtx 4050")) {
            return 75;
        }

        if (value.contains("rtx")) {
            return 70;
        }

        if (value.contains("gtx")) {
            return 60;
        }

        return 40;
    }

    /*
     * Lower laptop weight receives a higher score.
     * Weight is assumed to be stored in kilograms.
     */
    private double calculateWeightScore(Product product) {
        Double weight = getSpecificationNumericValue(
                product, "Weight");

        if (weight == null || weight <= 0) {
            return 0;
        }

        double score = 100 - ((weight - 1.2) / 1.8) * 100;

        return clampScore(score);
    }

    /*
     * Fetch a numeric specification from the database.
     */
    private Double getSpecificationNumericValue(
            Product product,
            String specificationName) {

        List<ProductSpecification> specifications =
                productSpecificationRepository.findByProductId(product.getId());

        if (specifications == null) {
            return null;
        }

        for (ProductSpecification specification : specifications) {
            if (specification.getSpecificationName() != null
                    && specification.getSpecificationName()
                    .equalsIgnoreCase(specificationName)) {
                return specification.getNumericValue();
            }
        }

        return null;
    }

    /*
     * Fetch a text specification from the database.
     */
    private String getSpecificationValue(
            Product product,
            String specificationName) {

        List<ProductSpecification> specifications =
                productSpecificationRepository.findByProductId(product.getId());

        if (specifications == null) {
            return null;
        }

        for (ProductSpecification specification : specifications) {
            if (specification.getSpecificationName() != null
                    && specification.getSpecificationName()
                    .equalsIgnoreCase(specificationName)) {
                return specification.getSpecificationValue();
            }
        }

        return null;
    }

    /*
     * Keep every score within the 0-100 range.
     */
    private double clampScore(double score) {
        return Math.max(0, Math.min(100, score));
    }

    /*
     * Personalized scoring.
     *
     * User-defined price and rating weights are normalized.
     * Category intelligence contributes 70%; personal price/rating
     * preferences contribute 30%.
     */
    public double calculatePersonalizedScore(
            Product product,
            double priceWeight,
            double ratingWeight) {

        double totalWeight = priceWeight + ratingWeight;

        if (priceWeight < 0 || ratingWeight < 0) {
            priceWeight = 0.30;
            ratingWeight = 0.70;
            totalWeight = 1.0;
        }

        if (totalWeight <= 0) {
            priceWeight = 0.30;
            ratingWeight = 0.70;
            totalWeight = 1.0;
        }

        priceWeight = priceWeight / totalWeight;
        ratingWeight = ratingWeight / totalWeight;

        double categoryScore = calculateScore(product);
        double ratingScore = calculateRatingScore(product);
        double priceScore = calculatePriceScore(product);

        double personalizedBaseScore =
                ratingScore * ratingWeight
                        + priceScore * priceWeight;

        return clampScore(
                categoryScore * 0.70
                        + personalizedBaseScore * 0.30);
    }
}

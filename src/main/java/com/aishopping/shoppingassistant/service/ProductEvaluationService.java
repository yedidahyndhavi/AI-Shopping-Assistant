package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductEvaluationService {

    private final ProductSpecificationRepository
            productSpecificationRepository;

    public ProductEvaluationService(
            ProductSpecificationRepository
                    productSpecificationRepository) {

        this.productSpecificationRepository =
                productSpecificationRepository;
    }

    /*
     * Main scoring method.
     *
     * The scoring model changes according to the
     * product category.
     */
    public double calculateScore(Product product) {

        if (product.getCategory() == null) {
            return calculateBasicScore(product);
        }

        String category =
                product.getCategory().toLowerCase();

        if (category.contains("smartphone")) {
            return calculateSmartphoneScore(product);
        }

        if (category.contains("laptop")) {
            return calculateLaptopScore(product);
        }

        return calculateBasicScore(product);
    }

    /*
     * ---------------------------------------------------------
     * SMARTPHONE SCORING
     * ---------------------------------------------------------
     *
     * Rating       -> 20%
     * Price        -> 20%
     * RAM          -> 10%
     * Storage      -> 10%
     * Main Camera  -> 10%
     * Battery      -> 10%
     * Display      -> 5%
     * Refresh Rate -> 10%
     * 5G           -> 5%
     *
     * Total        -> 100%
     */
    private double calculateSmartphoneScore(
            Product product) {

        double ratingScore =
                calculateRatingScore(product);

        double priceScore =
                calculatePriceScore(product);

        double ramScore =
                calculateSpecificationScore(
                        product,
                        "RAM",
                        16);

        double storageScore =
                calculateSpecificationScore(
                        product,
                        "Storage",
                        512);

        double cameraScore =
                calculateSpecificationScore(
                        product,
                        "Main Camera",
                        200);

        double batteryScore =
                calculateSpecificationScore(
                        product,
                        "Battery",
                        6000);

        double displayScore =
                calculateSpecificationScore(
                        product,
                        "Display Size",
                        7.0);

        double refreshRateScore =
                calculateSpecificationScore(
                        product,
                        "Refresh Rate",
                        120);

        double fiveGScore =
                calculateBooleanSpecificationScore(
                        product,
                        "5G");

        return (ratingScore * 0.20)
                + (priceScore * 0.20)
                + (ramScore * 0.10)
                + (storageScore * 0.10)
                + (cameraScore * 0.10)
                + (batteryScore * 0.10)
                + (displayScore * 0.05)
                + (refreshRateScore * 0.10)
                + (fiveGScore * 0.05);
    }

    /*
     * ---------------------------------------------------------
     * LAPTOP SCORING
     * ---------------------------------------------------------
     *
     * Rating       -> 15%
     * Price        -> 20%
     * Processor    -> 10%
     * RAM          -> 15%
     * Storage      -> 10%
     * GPU          -> 10%
     * Display      -> 5%
     * Refresh Rate -> 5%
     * Battery      -> 5%
     * Weight       -> 5%
     *
     * Total        -> 100%
     */
    private double calculateLaptopScore(
            Product product) {

        double ratingScore =
                calculateRatingScore(product);

        double priceScore =
                calculatePriceScore(product);

        double processorScore =
                calculateProcessorScore(product);

        double ramScore =
                calculateSpecificationScore(
                        product,
                        "RAM",
                        32);

        double storageScore =
                calculateSpecificationScore(
                        product,
                        "Storage",
                        1024);

        double gpuScore =
                calculateGpuScore(product);

        double displayScore =
                calculateSpecificationScore(
                        product,
                        "Display Size",
                        17.3);

        double refreshRateScore =
                calculateSpecificationScore(
                        product,
                        "Refresh Rate",
                        240);

        double batteryScore =
                calculateSpecificationScore(
                        product,
                        "Battery",
                        100);

        double weightScore =
                calculateWeightScore(product);

        return (ratingScore * 0.15)
                + (priceScore * 0.20)
                + (processorScore * 0.10)
                + (ramScore * 0.15)
                + (storageScore * 0.10)
                + (gpuScore * 0.10)
                + (displayScore * 0.05)
                + (refreshRateScore * 0.05)
                + (batteryScore * 0.05)
                + (weightScore * 0.05);
    }

    /*
     * ---------------------------------------------------------
     * BASIC SCORING
     * ---------------------------------------------------------
     *
     * Used for categories that don't yet have
     * category-specific scoring rules.
     */
    private double calculateBasicScore(
            Product product) {

        double ratingScore =
                calculateRatingScore(product);

        double priceScore =
                calculatePriceScore(product);

        return (ratingScore * 0.60)
                + (priceScore * 0.40);
    }

    /*
     * Rating score: 0 - 100
     */
    private double calculateRatingScore(
            Product product) {

        return (product.getRating() / 5.0) * 100;
    }

    /*
     * Price score:
     * Lower price receives a higher score.
     */
    private double calculatePriceScore(
            Product product) {

        return Math.max(
                0,
                100 - (product.getPrice() / 100000) * 100
        );
    }

    /*
     * Generic numeric specification score.
     *
     * Example:
     * RAM = 12 GB
     * Maximum reference = 16 GB
     *
     * Score = 12 / 16 * 100
     */
    private double calculateSpecificationScore(
            Product product,
            String specificationName,
            double maximumValue) {

        Double value =
                getSpecificationNumericValue(
                        product,
                        specificationName);

        if (value == null || maximumValue <= 0) {
            return 0;
        }

        return Math.min(
                100,
                (value / maximumValue) * 100
        );
    }

    /*
     * 5G scoring.
     */
    private double calculateBooleanSpecificationScore(
            Product product,
            String specificationName) {

        String value =
                getSpecificationValue(
                        product,
                        specificationName);

        if (value == null) {
            return 0;
        }

        if (value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("true")) {

            return 100;
        }

        return 0;
    }

    /*
     * Processor scoring for laptops.
     */
    private double calculateProcessorScore(
            Product product) {

        String processor =
                getSpecificationValue(
                        product,
                        "Processor");

        if (processor == null) {
            return 0;
        }

        String value =
                processor.toLowerCase();

        if (value.contains("m4")) {
            return 100;
        }

        if (value.contains("i9")) {
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
     * GPU scoring for laptops.
     */
    private double calculateGpuScore(
            Product product) {

        String gpu =
                getSpecificationValue(
                        product,
                        "GPU");

        if (gpu == null) {
            return 0;
        }

        String value =
                gpu.toLowerCase();

        if (value.contains("rtx 4090")) {
            return 100;
        }

        if (value.contains("rtx 4080")) {
            return 95;
        }

        if (value.contains("rtx 4070")) {
            return 90;
        }

        if (value.contains("rtx 4060")) {
            return 85;
        }

        if (value.contains("rtx 4050")) {
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
     */
    private double calculateWeightScore(
            Product product) {

        Double weight =
                getSpecificationNumericValue(
                        product,
                        "Weight");

        if (weight == null) {
            return 0;
        }

        /*
         * 1.2 kg -> approximately 100
         * 3.0 kg -> approximately 0
         */
        double score =
                100
                - ((weight - 1.2) / 1.8) * 100;

        return Math.max(
                0,
                Math.min(100, score));
    }

    /*
     * Get numeric specification value.
     */
    private Double getSpecificationNumericValue(
            Product product,
            String specificationName) {

        List<ProductSpecification> specifications =
                productSpecificationRepository
                        .findByProductId(product.getId());

        for (ProductSpecification specification :
                specifications) {

            if (specification.getSpecificationName()
                    .equalsIgnoreCase(specificationName)) {

                return specification.getNumericValue();
            }
        }

        return null;
    }

    /*
     * Get text specification value.
     */
    private String getSpecificationValue(
            Product product,
            String specificationName) {

        List<ProductSpecification> specifications =
                productSpecificationRepository
                        .findByProductId(product.getId());

        for (ProductSpecification specification :
                specifications) {

            if (specification.getSpecificationName()
                    .equalsIgnoreCase(specificationName)) {

                return specification.getSpecificationValue();
            }
        }

        return null;
    }

    /*
     * Personalized scoring.
     *
     * Existing API support is preserved.
     * User-defined price/rating weights are still respected.
     */
    public double calculatePersonalizedScore(
            Product product,
            double priceWeight,
            double ratingWeight) {

        double totalWeight =
                priceWeight + ratingWeight;

        if (totalWeight <= 0) {
            priceWeight = 0.3;
            ratingWeight = 0.7;
            totalWeight = 1.0;
        }

        priceWeight =
                priceWeight / totalWeight;

        ratingWeight =
                ratingWeight / totalWeight;

        double categoryScore =
                calculateScore(product);

        double ratingScore =
                calculateRatingScore(product);

        double priceScore =
                calculatePriceScore(product);

        /*
         * Preserve user preference while also
         * incorporating category intelligence.
         */
        double personalizedBaseScore =
                (ratingScore * ratingWeight)
                + (priceScore * priceWeight);

        return (categoryScore * 0.70)
                + (personalizedBaseScore * 0.30);
    }
}
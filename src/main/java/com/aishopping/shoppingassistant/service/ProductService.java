package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.repository.ProductRepository;
import com.aishopping.shoppingassistant.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductEvaluationService productEvaluationService;

    private final ProductSpecificationRepository
            productSpecificationRepository;


    // ============================================================
    // Constructor
    // ============================================================

    public ProductService(
            ProductRepository productRepository,
            ProductEvaluationService productEvaluationService,
            ProductSpecificationRepository
                    productSpecificationRepository) {

        this.productRepository = productRepository;

        this.productEvaluationService =
                productEvaluationService;

        this.productSpecificationRepository =
                productSpecificationRepository;
    }


    // ============================================================
    // Get all products
    // ============================================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }


    // ============================================================
    // Get only available products
    // ============================================================

    public List<Product> getAvailableProducts() {

        return productRepository.findByAvailableTrue();
    }


    // ============================================================
    // Get product by ID
    // ============================================================

    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"));
    }


    // ============================================================
    // Add a new product
    // ============================================================

    public Product addProduct(Product product) {

        return productRepository.save(product);
    }


    // ============================================================
    // Update an existing product
    // ============================================================

    public Product updateProduct(
            Long id,
            Product updatedProduct) {

        Product existingProduct =
                getProductById(id);

        existingProduct.setName(
                updatedProduct.getName());

        existingProduct.setBrand(
                updatedProduct.getBrand());

        existingProduct.setPrice(
                updatedProduct.getPrice());

        existingProduct.setCategory(
                updatedProduct.getCategory());

        existingProduct.setRating(
                updatedProduct.getRating());

        existingProduct.setDescription(
                updatedProduct.getDescription());

        existingProduct.setAvailable(
                updatedProduct.isAvailable());

        return productRepository.save(
                existingProduct);
    }


    // ============================================================
    // Search products by name
    // ============================================================

    public List<Product> searchProducts(
            String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name);
    }


    // ============================================================
    // Filter products by category
    // ============================================================

    public List<Product> filterByCategory(
            String category) {

        return productRepository
                .findByCategoryIgnoreCase(category);
    }


    // ============================================================
    // Filter products by maximum price
    // ============================================================

    public List<Product> filterByMaxPrice(
            double maxPrice) {

        return productRepository
                .findByPriceLessThanEqual(maxPrice);
    }


    // ============================================================
    // Filter products by minimum rating
    // ============================================================

    public List<Product> filterByMinRating(
            double minRating) {

        return productRepository
                .findByRatingGreaterThanEqual(minRating);
    }


    // ============================================================
    // Basic product comparison
    // ============================================================

    public ProductComparison compareProducts(
            List<Long> ids) {

        if (ids == null || ids.size() != 2) {

            throw new IllegalArgumentException(
                    "Exactly two product IDs are required");
        }

        Product product1 =
                getProductById(ids.get(0));

        Product product2 =
                getProductById(ids.get(1));

        double priceDifference =
                Math.abs(
                        product1.getPrice()
                                - product2.getPrice());

        String cheaperProduct;

        if (product1.getPrice()
                < product2.getPrice()) {

            cheaperProduct =
                    product1.getName();

        } else if (product2.getPrice()
                < product1.getPrice()) {

            cheaperProduct =
                    product2.getName();

        } else {

            cheaperProduct =
                    "Both products have the same price";
        }

        return new ProductComparison(
                product1,
                product2,
                priceDifference,
                cheaperProduct);
    }


    // ============================================================
    // Detailed product comparison
    // ============================================================

    public ProductComparisonResponse compareProductsDetailed(
            List<Long> ids) {

        if (ids == null || ids.size() != 2) {

            throw new IllegalArgumentException(
                    "Exactly two product IDs are required");
        }

        Product product1 =
                getProductById(ids.get(0));

        Product product2 =
                getProductById(ids.get(1));


        // ========================================================
        // Price comparison
        // ========================================================

        double priceDifference =
                Math.abs(
                        product1.getPrice()
                                - product2.getPrice());

        Product cheaperProduct = null;

        if (product1.getPrice()
                < product2.getPrice()) {

            cheaperProduct = product1;

        } else if (product2.getPrice()
                < product1.getPrice()) {

            cheaperProduct = product2;
        }


        // ========================================================
        // Rating comparison
        // ========================================================

        double ratingDifference =
                Math.abs(
                        product1.getRating()
                                - product2.getRating());

        Product higherRatedProduct = null;

        if (product1.getRating()
                > product2.getRating()) {

            higherRatedProduct = product1;

        } else if (product2.getRating()
                > product1.getRating()) {

            higherRatedProduct = product2;
        }


        // ========================================================
        // Calculate evaluation scores
        // ========================================================

        double product1Score =
                productEvaluationService
                        .calculateScore(product1);

        double product2Score =
                productEvaluationService
                        .calculateScore(product2);


        // ========================================================
        // Compare specifications
        // ========================================================

        List<Map<String, Object>>
                specificationComparisons =
                compareSpecifications(
                        product1,
                        product2);


        // ========================================================
        // Generate comparison summary
        // ========================================================

        String comparisonSummary;

        if (product1Score > product2Score) {

            comparisonSummary =
                    product1.getName()
                            + " has a higher overall score than "
                            + product2.getName();

        } else if (product2Score > product1Score) {

            comparisonSummary =
                    product2.getName()
                            + " has a higher overall score than "
                            + product1.getName();

        } else {

            comparisonSummary =
                    "Both products have the same overall score";
        }


        // ========================================================
        // Create response
        // ========================================================

        ProductComparisonResponse response =
                new ProductComparisonResponse(
                        product1,
                        product2,
                        priceDifference,
                        cheaperProduct,
                        ratingDifference,
                        higherRatedProduct,
                        product1Score,
                        product2Score,
                        comparisonSummary);


        response.setSpecificationComparisons(
                specificationComparisons);


        return response;
    }


    // ============================================================
    // Compare product specifications
    // ============================================================

    private List<Map<String, Object>>
    compareSpecifications(
            Product product1,
            Product product2) {

        List<Map<String, Object>>
                comparisons =
                new ArrayList<>();

        String category =
                product1.getCategory();

        if (category == null) {

            return comparisons;
        }


        // ========================================================
        // Get specifications from database
        // ========================================================

        List<ProductSpecification>
                specifications1 =
                productSpecificationRepository
                        .findByProductId(
                                product1.getId());

        List<ProductSpecification>
                specifications2 =
                productSpecificationRepository
                        .findByProductId(
                                product2.getId());


        // ========================================================
        // Convert Product 1 specifications into a map
        // ========================================================

        Map<String, ProductSpecification>
                product1Specifications =
                new LinkedHashMap<>();

        for (ProductSpecification specification :
                specifications1) {

            if (specification.getSpecificationName()
                    != null) {

                product1Specifications.put(
                        specification
                                .getSpecificationName()
                                .toLowerCase(),
                        specification);
            }
        }


        // ========================================================
        // Convert Product 2 specifications into a map
        // ========================================================

        Map<String, ProductSpecification>
                product2Specifications =
                new LinkedHashMap<>();

        for (ProductSpecification specification :
                specifications2) {

            if (specification.getSpecificationName()
                    != null) {

                product2Specifications.put(
                        specification
                                .getSpecificationName()
                                .toLowerCase(),
                        specification);
            }
        }


        // ========================================================
        // Get category-specific specifications
        // ========================================================

        List<String> relevantSpecifications =
                getRelevantSpecifications(category);


        // ========================================================
        // Compare each specification
        // ========================================================

        for (String specificationName :
                relevantSpecifications) {

            ProductSpecification specification1 =
                    product1Specifications.get(
                            specificationName.toLowerCase());

            ProductSpecification specification2 =
                    product2Specifications.get(
                            specificationName.toLowerCase());


            Map<String, Object> comparison =
                    new LinkedHashMap<>();


            // Specification name
            comparison.put(
                    "specification",
                    specificationName);


            // Product 1 value
            comparison.put(
                    "product1Value",
                    getDisplayValue(
                            specification1));


            // Product 2 value
            comparison.put(
                    "product2Value",
                    getDisplayValue(
                            specification2));


            // Winner
            comparison.put(
                    "winner",
                    determineWinner(
                            specificationName,
                            specification1,
                            specification2,
                            product1,
                            product2));


            comparisons.add(comparison);
        }


        return comparisons;
    }


    // ============================================================
    // Get relevant specifications based on category
    // ============================================================

    private List<String>
    getRelevantSpecifications(
            String category) {

        List<String> specifications =
                new ArrayList<>();

        String categoryLower =
                category.toLowerCase();


        // ========================================================
        // Smartphone
        // ========================================================

        if (categoryLower.contains("smartphone")) {

            specifications.add("RAM");
            specifications.add("Storage");
            specifications.add("Main Camera");
            specifications.add("Battery");
            specifications.add("Display Size");
            specifications.add("Refresh Rate");
            specifications.add("5G");

            return specifications;
        }


        // ========================================================
        // Laptop
        // ========================================================

        if (categoryLower.contains("laptop")) {

            specifications.add("Processor");
            specifications.add("RAM");
            specifications.add("Storage");
            specifications.add("GPU");
            specifications.add("Display Size");
            specifications.add("Refresh Rate");
            specifications.add("Battery");
            specifications.add("Weight");
            specifications.add("Operating System");

            return specifications;
        }


        return specifications;
    }


    // ============================================================
    // Get display value
    // ============================================================

    private String getDisplayValue(
            ProductSpecification specification) {

        if (specification == null) {

            return "Not available";
        }

        String value =
                specification.getSpecificationValue();

        String unit =
                specification.getUnit();


        if (value == null
                || value.trim().isEmpty()) {

            return "Not available";
        }


        if (unit == null
                || unit.trim().isEmpty()) {

            return value;
        }


        /*
         * Prevent duplicate units.
         *
         * Example:
         *
         * value = "60 Hz"
         * unit  = "Hz"
         *
         * Result:
         * "60 Hz"
         *
         * instead of:
         * "60 Hz Hz"
         */

        if (value.toLowerCase()
                .contains(unit.toLowerCase())) {

            return value;
        }


        return value + " " + unit;
    }


    // ============================================================
    // Determine specification winner
    // ============================================================

    private String determineWinner(
            String specificationName,
            ProductSpecification specification1,
            ProductSpecification specification2,
            Product product1,
            Product product2) {


        // ========================================================
        // Both specifications missing
        // ========================================================

        if (specification1 == null
                && specification2 == null) {

            return "Not available";
        }


        // ========================================================
        // Product 1 specification missing
        // ========================================================

        if (specification1 == null) {

            return product2.getName();
        }


        // ========================================================
        // Product 2 specification missing
        // ========================================================

        if (specification2 == null) {

            return product1.getName();
        }


        // ========================================================
        // 5G comparison
        // ========================================================

        if (specificationName
                .equalsIgnoreCase("5G")) {

            String value1 =
                    specification1
                            .getSpecificationValue();

            String value2 =
                    specification2
                            .getSpecificationValue();


            boolean product1HasFeature =
                    value1 != null
                            && (value1.equalsIgnoreCase("yes")
                            || value1.equalsIgnoreCase("true"));


            boolean product2HasFeature =
                    value2 != null
                            && (value2.equalsIgnoreCase("yes")
                            || value2.equalsIgnoreCase("true"));


            if (product1HasFeature
                    && !product2HasFeature) {

                return product1.getName();
            }


            if (product2HasFeature
                    && !product1HasFeature) {

                return product2.getName();
            }


            return "Tie";
        }


        // ========================================================
        // Processor comparison
        // ========================================================

        if (specificationName
                .equalsIgnoreCase("Processor")) {

            return compareProcessor(
                    specification1
                            .getSpecificationValue(),
                    specification2
                            .getSpecificationValue(),
                    product1,
                    product2);
        }


        // ========================================================
        // GPU comparison
        // ========================================================

        if (specificationName
                .equalsIgnoreCase("GPU")) {

            return compareGpu(
                    specification1
                            .getSpecificationValue(),
                    specification2
                            .getSpecificationValue(),
                    product1,
                    product2);
        }


        // ========================================================
        // Numeric comparison
        // ========================================================

        Double value1 =
                specification1.getNumericValue();

        Double value2 =
                specification2.getNumericValue();


        if (value1 != null
                && value2 != null) {


            // ====================================================
            // Lower weight is better
            // ====================================================

            if (specificationName
                    .equalsIgnoreCase("Weight")) {

                if (value1 < value2) {

                    return product1.getName();

                } else if (value2 < value1) {

                    return product2.getName();

                } else {

                    return "Tie";
                }
            }


            // ====================================================
            // Higher numeric value is better
            // ====================================================

            if (value1 > value2) {

                return product1.getName();

            } else if (value2 > value1) {

                return product2.getName();

            } else {

                return "Tie";
            }
        }


        // ========================================================
        // Text comparison
        // ========================================================

        String value1Text =
                specification1
                        .getSpecificationValue();

        String value2Text =
                specification2
                        .getSpecificationValue();


        if (value1Text != null
                && value1Text.equalsIgnoreCase(
                        value2Text)) {

            return "Tie";
        }


        return "Not determined";
    }


    // ============================================================
    // Compare processors
    // ============================================================

    private String compareProcessor(
            String processor1,
            String processor2,
            Product product1,
            Product product2) {

        int score1 =
                getProcessorScore(processor1);

        int score2 =
                getProcessorScore(processor2);


        if (score1 > score2) {

            return product1.getName();

        } else if (score2 > score1) {

            return product2.getName();

        } else {

            return "Tie";
        }
    }


    // ============================================================
    // Processor scoring
    // ============================================================

    private int getProcessorScore(
            String processor) {

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


        return 40;
    }


    // ============================================================
    // Compare GPUs
    // ============================================================

    private String compareGpu(
            String gpu1,
            String gpu2,
            Product product1,
            Product product2) {

        int score1 =
                getGpuScore(gpu1);

        int score2 =
                getGpuScore(gpu2);


        if (score1 > score2) {

            return product1.getName();

        } else if (score2 > score1) {

            return product2.getName();

        } else {

            return "Tie";
        }
    }


    // ============================================================
    // GPU scoring
    // ============================================================

    private int getGpuScore(
            String gpu) {

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
}
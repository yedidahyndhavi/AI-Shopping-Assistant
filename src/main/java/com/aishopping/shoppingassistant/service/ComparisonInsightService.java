
package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.ComparisonInsight;
import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductSpecification;
import com.aishopping.shoppingassistant.repository.ProductSpecificationRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComparisonInsightService {

    private final ProductService productService;
    private final ProductSpecificationRepository specificationRepository;
    private final ProductEvaluationService evaluationService;

    public ComparisonInsightService(
            ProductService productService,
            ProductSpecificationRepository specificationRepository,
            ProductEvaluationService evaluationService) {

        this.productService = productService;
        this.specificationRepository = specificationRepository;
        this.evaluationService = evaluationService;
    }

    public ComparisonInsight generateInsight(
            Long product1Id,
            Long product2Id) {

        if (product1Id == null || product2Id == null) {
            throw new IllegalArgumentException(
                    "Both product IDs are required.");
        }

        if (product1Id.equals(product2Id)) {
            throw new IllegalArgumentException(
                    "Choose two different products to compare.");
        }

        Product p1 = findProduct(product1Id);
        Product p2 = findProduct(product2Id);

        List<String> strengths1 = new ArrayList<>();
        List<String> strengths2 = new ArrayList<>();
        List<String> tradeOffs = new ArrayList<>();

        // Compare price
        if (p1.getPrice() < p2.getPrice()) {
            strengths1.add("Lower price");
            tradeOffs.add(p1.getName() + " costs ₹"
                    + formatNumber(p2.getPrice() - p1.getPrice())
                    + " less");
        } else if (p2.getPrice() < p1.getPrice()) {
            strengths2.add("Lower price");
            tradeOffs.add(p2.getName() + " costs ₹"
                    + formatNumber(p1.getPrice() - p2.getPrice())
                    + " less");
        } else {
            tradeOffs.add("Both products have the same price.");
        }

        // Compare customer ratings
        if (p1.getRating() > p2.getRating()) {
            strengths1.add("Higher customer rating");
        } else if (p2.getRating() > p1.getRating()) {
            strengths2.add("Higher customer rating");
        } else {
            tradeOffs.add("Both products have the same rating.");
        }

        // Compare common numeric specifications
        String[] specifications = {
                "RAM",
                "Storage",
                "Main Camera",
                "Battery",
                "Display Size",
                "Refresh Rate",
                "Weight"
        };

        for (String name : specifications) {
            compareNumericSpecification(
                    p1, p2, name,
                    strengths1, strengths2, tradeOffs);
        }

        // Compare processor and GPU for laptops
        if ("Laptop".equalsIgnoreCase(p1.getCategory())
                && "Laptop".equalsIgnoreCase(p2.getCategory())) {

            compareTextSpecification(
                    p1, p2, "Processor",
                    strengths1, strengths2, tradeOffs);

            compareTextSpecification(
                    p1, p2, "GPU",
                    strengths1, strengths2, tradeOffs);

            compareTextSpecification(
                    p1, p2, "Operating System",
                    strengths1, strengths2, tradeOffs);
        }

        // Calculate overall scores using the existing evaluation service
        double score1 = evaluationService.calculateScore(p1);
        double score2 = evaluationService.calculateScore(p2);

        String winner;
        String summary;

        if (score1 > score2) {
            winner = p1.getName();
            summary = winner
                    + " has the higher overall score based on the current scoring engine.";
        } else if (score2 > score1) {
            winner = p2.getName();
            summary = winner
                    + " has the higher overall score based on the current scoring engine.";
        } else {
            winner = "Tie";
            summary = "Both products have the same overall score. "
                    + "Choose based on your priorities.";
        }

        return new ComparisonInsight(
                p1.getName(),
                p2.getName(),
                winner,
                summary,
                strengths1,
                strengths2,
                tradeOffs);
    }

    private Product findProduct(Long id) {
        return productService.getAllProducts()
                .stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found: " + id));
    }

    private ProductSpecification findSpecification(
            Long productId,
            String name) {

        return specificationRepository.findByProductId(productId)
                .stream()
                .filter(s -> s.getSpecificationName() != null
                        && s.getSpecificationName()
                                .trim()
                                .equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private void compareNumericSpecification(
            Product p1,
            Product p2,
            String name,
            List<String> strengths1,
            List<String> strengths2,
            List<String> tradeOffs) {

        ProductSpecification s1 =
                findSpecification(p1.getId(), name);

        ProductSpecification s2 =
                findSpecification(p2.getId(), name);

        // Report missing or non-numeric values
        if (s1 == null || s2 == null
                || s1.getNumericValue() == null
                || s2.getNumericValue() == null) {

            String value1 = (s1 == null
                    || s1.getNumericValue() == null)
                    ? "Not available"
                    : displayValue(s1);

            String value2 = (s2 == null
                    || s2.getNumericValue() == null)
                    ? "Not available"
                    : displayValue(s2);

            tradeOffs.add(name + ": "
                    + p1.getName() + " = " + value1
                    + ", " + p2.getName() + " = " + value2);

            return;
        }

        double v1 = s1.getNumericValue();
        double v2 = s2.getNumericValue();

        if (Double.compare(v1, v2) == 0) {
            tradeOffs.add(name + ": Both products have "
                    + displayValue(s1));
            return;
        }

        // Lower weight is preferable; higher values for other
        // numeric specifications are treated as preferable.
        boolean lowerIsBetter = "Weight".equalsIgnoreCase(name);

        if ((lowerIsBetter && v1 < v2)
                || (!lowerIsBetter && v1 > v2)) {

            strengths1.add(
                    (lowerIsBetter ? "Lower " : "Higher ") + name);

        } else {
            strengths2.add(
                    (lowerIsBetter ? "Lower " : "Higher ") + name);
        }

        tradeOffs.add(name + ": "
                + p1.getName() + " = " + displayValue(s1)
                + ", " + p2.getName() + " = " + displayValue(s2));
    }

    private void compareTextSpecification(
            Product p1,
            Product p2,
            String name,
            List<String> strengths1,
            List<String> strengths2,
            List<String> tradeOffs) {

        ProductSpecification s1 =
                findSpecification(p1.getId(), name);

        ProductSpecification s2 =
                findSpecification(p2.getId(), name);

        if (s1 == null || s2 == null
                || s1.getSpecificationValue() == null
                || s2.getSpecificationValue() == null) {

            String value1 = (s1 == null
                    || s1.getSpecificationValue() == null)
                    ? "Not available"
                    : displayValue(s1);

            String value2 = (s2 == null
                    || s2.getSpecificationValue() == null)
                    ? "Not available"
                    : displayValue(s2);

            tradeOffs.add(name + ": "
                    + p1.getName() + " = " + value1
                    + ", " + p2.getName() + " = " + value2);

            return;
        }

        String value1 = s1.getSpecificationValue().trim();
        String value2 = s2.getSpecificationValue().trim();

        if (!value1.equalsIgnoreCase(value2)) {
            tradeOffs.add(name + ": "
                    + p1.getName() + " = " + value1
                    + ", " + p2.getName() + " = " + value2);
        } else {
            tradeOffs.add(name + ": Both products have " + value1);
        }
    }

    private String displayValue(ProductSpecification specification) {
        String value = specification.getSpecificationValue();
        String unit = specification.getUnit();

        if (value == null || value.isBlank()) {
            value = specification.getNumericValue() == null
                    ? "Not available"
                    : String.valueOf(specification.getNumericValue());
        }

        if (unit == null || unit.isBlank()
                || value.toLowerCase().contains(unit.toLowerCase())) {
            return value;
        }

        return value + " " + unit;
    }

    private String formatNumber(double number) {
        return String.format("%.0f", number);
    }
}

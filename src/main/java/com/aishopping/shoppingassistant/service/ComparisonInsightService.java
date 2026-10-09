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
            tradeOffs.add("Both products have the same price");
        }

        // Compare customer ratings
        if (p1.getRating() > p2.getRating()) {
            strengths1.add("Higher customer rating");
        } else if (p2.getRating() > p1.getRating()) {
            strengths2.add("Higher customer rating");
        } else {
            tradeOffs.add("Both products have the same rating");
        }

        // Compare common numeric specifications
        String[] specifications = {
                "RAM", "Storage", "Main Camera", "Battery",
                "Display Size", "Refresh Rate", "Weight"
        };

        for (String name : specifications) {
            compareNumericSpecification(
                    p1, p2, name, strengths1, strengths2, tradeOffs);
        }

        // Compare category-specific laptop specifications
        if ("Laptop".equalsIgnoreCase(p1.getCategory())
                && "Laptop".equalsIgnoreCase(p2.getCategory())) {
            compareTextSpecification(
                    p1, p2, "Processor", strengths1, strengths2, tradeOffs);
            compareTextSpecification(
                    p1, p2, "GPU", strengths1, strengths2, tradeOffs);
        }

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
            summary = "Both products have the same overall score. Choose based on your priorities.";
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
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found: " + id));
    }

    private ProductSpecification findSpecification(
            Long productId, String name) {
        return specificationRepository.findByProductId(productId)
                .stream()
                .filter(s -> s.getSpecificationName() != null
                        && s.getSpecificationName().equalsIgnoreCase(name))
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

        ProductSpecification s1 = findSpecification(p1.getId(), name);
        ProductSpecification s2 = findSpecification(p2.getId(), name);

        if (s1 == null || s2 == null
                || s1.getNumericValue() == null
                || s2.getNumericValue() == null) {
            return;
        }

        double v1 = s1.getNumericValue();
        double v2 = s2.getNumericValue();

        if (Double.compare(v1, v2) == 0) {
            return;
        }

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

        ProductSpecification s1 = findSpecification(p1.getId(), name);
        ProductSpecification s2 = findSpecification(p2.getId(), name);

        if (s1 == null || s2 == null
                || s1.getSpecificationValue() == null
                || s2.getSpecificationValue() == null) {
            return;
        }

        if (!s1.getSpecificationValue().equalsIgnoreCase(
                s2.getSpecificationValue())) {
            tradeOffs.add(name + ": "
                    + p1.getName() + " = " + s1.getSpecificationValue()
                    + ", " + p2.getName() + " = " + s2.getSpecificationValue());
        }
    }

    private String displayValue(ProductSpecification specification) {
        String value = specification.getSpecificationValue();
        String unit = specification.getUnit();

        if (value == null) {
            return "";
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
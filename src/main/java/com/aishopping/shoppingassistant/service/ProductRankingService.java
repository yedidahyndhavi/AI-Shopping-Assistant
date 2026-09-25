package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductRankingService {

    private final ProductEvaluationService productEvaluationService;

    public ProductRankingService(
            ProductEvaluationService productEvaluationService) {

        this.productEvaluationService = productEvaluationService;
    }

    // =========================================================
    // GENERAL PRODUCT RANKING
    // =========================================================

    public List<Product> rankProducts(
            List<Product> products) {

        return products.stream()
                .sorted(
                        Comparator.comparingDouble(
                                productEvaluationService::calculateScore
                        ).reversed()
                )
                .toList();
    }

    // =========================================================
    // DAY 24 - PREFERENCE-AWARE PRODUCT RANKING
    // =========================================================

    public List<Product> rankProductsByPreference(
            List<Product> products,
            double priceWeight,
            double ratingWeight) {

        return products.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (Product product) ->
                                        productEvaluationService
                                                .calculatePersonalizedScore(
                                                        product,
                                                        priceWeight,
                                                        ratingWeight
                                                )
                        ).reversed()
                )
                .toList();
    }
}
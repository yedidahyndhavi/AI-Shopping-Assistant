package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductEvaluationService {

    public double calculateScore(Product product) {

        double ratingScore = (product.getRating() / 5.0) * 70;

        double priceScore = Math.max(
                0,
                30 - (product.getPrice() / 100000) * 30
        );

        return ratingScore + priceScore;
    }
    public double calculatePersonalizedScore(
        Product product,
        double priceWeight,
        double ratingWeight) {

    double totalWeight = priceWeight + ratingWeight;

    if (totalWeight <= 0) {
        priceWeight = 0.3;
        ratingWeight = 0.7;
        totalWeight = 1.0;
    }

    priceWeight = priceWeight / totalWeight;
    ratingWeight = ratingWeight / totalWeight;

    double ratingScore = (product.getRating() / 5.0) * 100;

    double priceScore = Math.max(
            0,
            100 - (product.getPrice() / 100000) * 100
    );

    return (ratingScore * ratingWeight)
            + (priceScore * priceWeight);
}
}
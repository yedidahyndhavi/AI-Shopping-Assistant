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
}
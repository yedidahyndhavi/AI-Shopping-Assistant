package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.RecommendationRequest;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NaturalLanguageQueryService {

    public RecommendationRequest parseQuery(String query) {

        String category = extractCategory(query);
        double maxPrice = extractMaxPrice(query);
        double minRating = extractMinRating(query);

        return new RecommendationRequest(
                category,
                maxPrice,
                minRating
        );
    }

    private String extractCategory(String query) {

        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("smartphone")
                || lowerQuery.contains("phone")) {
            return "Smartphone";
        }

        if (lowerQuery.contains("laptop")
                || lowerQuery.contains("macbook")) {
            return "Laptop";
        }

        return "";
    }

    private double extractMaxPrice(String query) {

        Pattern pattern = Pattern.compile(
                "(?:under|below|less than|maximum|max)\\s*(?:₹|rs\\.?|inr)?\\s*(\\d+(?:,\\d+)*)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return Double.parseDouble(
                    matcher.group(1).replace(",", "")
            );
        }

        return Double.MAX_VALUE;
    }

    private double extractMinRating(String query) {

        Pattern pattern = Pattern.compile(
                "(?:rating|rated)\\s*(?:above|over|greater than|at least)?\\s*(\\d+(?:\\.\\d+)?)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return 0.0;
    }
}
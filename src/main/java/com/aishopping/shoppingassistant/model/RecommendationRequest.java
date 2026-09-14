package com.aishopping.shoppingassistant.model;

public class RecommendationRequest {

    private String category;
    private double maxPrice;
    private double minRating;

    public RecommendationRequest() {
    }

    public RecommendationRequest(String category,
                                  double maxPrice,
                                  double minRating) {
        this.category = category;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getMinRating() {
        return minRating;
    }

    public void setMinRating(double minRating) {
        this.minRating = minRating;
    }
}
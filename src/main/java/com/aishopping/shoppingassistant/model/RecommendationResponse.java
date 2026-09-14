package com.aishopping.shoppingassistant.model;

public class RecommendationResponse {

    private Product recommendedProduct;
    private double score;
    private String reason;

    public RecommendationResponse() {
    }

    public RecommendationResponse(Product recommendedProduct,
                                   double score,
                                   String reason) {
        this.recommendedProduct = recommendedProduct;
        this.score = score;
        this.reason = reason;
    }

    public Product getRecommendedProduct() {
        return recommendedProduct;
    }

    public void setRecommendedProduct(Product recommendedProduct) {
        this.recommendedProduct = recommendedProduct;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
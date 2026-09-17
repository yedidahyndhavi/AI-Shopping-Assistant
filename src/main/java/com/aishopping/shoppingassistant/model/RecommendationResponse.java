package com.aishopping.shoppingassistant.model;

public class RecommendationResponse {

    private Product recommendedProduct;
    private double score;
    private String reason;
    private RecommendationExplanation explanation;

    public RecommendationResponse() {
    }

    public RecommendationResponse(Product recommendedProduct,
                                   double score,
                                   String reason) {
        this.recommendedProduct = recommendedProduct;
        this.score = score;
        this.reason = reason;
    }
    public RecommendationResponse(
        Product recommendedProduct,
        double score,
        String reason,
        RecommendationExplanation explanation) {

    this.recommendedProduct = recommendedProduct;
    this.score = score;
    this.reason = reason;
    this.explanation = explanation;
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

    public RecommendationExplanation getExplanation() {
        return explanation;
    }

    public void setExplanation(RecommendationExplanation explanation) {
        this.explanation = explanation;
    }
}
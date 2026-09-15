package com.aishopping.shoppingassistant.model;

import java.util.List;

public class RecommendationListResponse {

    private List<RecommendationResponse> recommendations;

    public RecommendationListResponse() {
    }

    public RecommendationListResponse(
            List<RecommendationResponse> recommendations) {
        this.recommendations = recommendations;
    }

    public List<RecommendationResponse> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<RecommendationResponse> recommendations) {
        this.recommendations = recommendations;
    }
}
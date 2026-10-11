
package com.aishopping.shoppingassistant.model;

import java.util.ArrayList;
import java.util.List;

public class RecommendationExplanation {

    private String budgetMessage;
    private String ratingMessage;
    private String preferenceMessage;
    private String brandMessage;

    private List<String> matchedRequirements = new ArrayList<>();
    private List<String> unmatchedRequirements = new ArrayList<>();
    private String recommendationReason;

    public RecommendationExplanation() {
    }

    public RecommendationExplanation(
            String budgetMessage,
            String ratingMessage,
            String preferenceMessage,
            String brandMessage) {

        this.budgetMessage = budgetMessage;
        this.ratingMessage = ratingMessage;
        this.preferenceMessage = preferenceMessage;
        this.brandMessage = brandMessage;
    }

    public String getBudgetMessage() {
        return budgetMessage;
    }

    public void setBudgetMessage(String budgetMessage) {
        this.budgetMessage = budgetMessage;
    }

    public String getRatingMessage() {
        return ratingMessage;
    }

    public void setRatingMessage(String ratingMessage) {
        this.ratingMessage = ratingMessage;
    }

    public String getPreferenceMessage() {
        return preferenceMessage;
    }

    public void setPreferenceMessage(String preferenceMessage) {
        this.preferenceMessage = preferenceMessage;
    }

    public String getBrandMessage() {
        return brandMessage;
    }

    public void setBrandMessage(String brandMessage) {
        this.brandMessage = brandMessage;
    }

    public List<String> getMatchedRequirements() {
        return matchedRequirements;
    }

    public void setMatchedRequirements(List<String> matchedRequirements) {
        this.matchedRequirements = matchedRequirements;
    }

    public List<String> getUnmatchedRequirements() {
        return unmatchedRequirements;
    }

    public void setUnmatchedRequirements(List<String> unmatchedRequirements) {
        this.unmatchedRequirements = unmatchedRequirements;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }
}

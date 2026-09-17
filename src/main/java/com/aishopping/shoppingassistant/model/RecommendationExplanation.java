package com.aishopping.shoppingassistant.model;

public class RecommendationExplanation {

    private String budgetMessage;
    private String ratingMessage;
    private String preferenceMessage;

    public RecommendationExplanation() {
    }

    public RecommendationExplanation(
            String budgetMessage,
            String ratingMessage,
            String preferenceMessage) {

        this.budgetMessage = budgetMessage;
        this.ratingMessage = ratingMessage;
        this.preferenceMessage = preferenceMessage;
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
}

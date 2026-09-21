package com.aishopping.shoppingassistant.model;

public class RecommendationExplanation {

    private String budgetMessage;
    private String ratingMessage;
    private String preferenceMessage;
    private String brandMessage;

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
    public String getBrandMessage() {
    return brandMessage;
}

public void setBrandMessage(String brandMessage) {
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
}

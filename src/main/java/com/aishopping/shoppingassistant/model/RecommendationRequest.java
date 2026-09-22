package com.aishopping.shoppingassistant.model;

public class RecommendationRequest {

    private String category;
    private double maxPrice;
    private double minRating;
    private double priceWeight;
    private double ratingWeight;
    private String preferredBrand;

    public RecommendationRequest() {
    }

    public RecommendationRequest(String category,
                                  double maxPrice,
                                  double minRating) {
        this.category = category;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    public void validate() {

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category is required");
        }

        if (maxPrice <= 0) {
            throw new IllegalArgumentException(
                    "Maximum price must be greater than 0");
        }

        if (minRating < 0 || minRating > 5) {
            throw new IllegalArgumentException(
                    "Minimum rating must be between 0 and 5");
        }

        if (priceWeight < 0 || ratingWeight < 0) {
            throw new IllegalArgumentException(
                    "Price weight and rating weight cannot be negative");
        }

        if (preferredBrand != null
                && preferredBrand.isBlank()) {

            preferredBrand = null;
        }
    }

    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

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

    public double getPriceWeight() {
        return priceWeight;
    }

    public void setPriceWeight(double priceWeight) {
        this.priceWeight = priceWeight;
    }

    public double getRatingWeight() {
        return ratingWeight;
    }

    public void setRatingWeight(double ratingWeight) {
        this.ratingWeight = ratingWeight;
    }

    public String getPreferredBrand() {
        return preferredBrand;
    }

    public void setPreferredBrand(String preferredBrand) {
        this.preferredBrand = preferredBrand;
    }
}
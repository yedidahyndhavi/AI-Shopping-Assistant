package com.aishopping.shoppingassistant.model;

public class RecommendationRequest {

    private String category;
    private double maxPrice;
    private double minRating;
    private double priceWeight;
private double ratingWeight;

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
}
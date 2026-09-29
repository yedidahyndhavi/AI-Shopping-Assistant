package com.aishopping.shoppingassistant.model;

public class ProductComparisonResponse {

    private Product product1;
    private Product product2;

    private double priceDifference;
    private Product cheaperProduct;

    private double ratingDifference;
    private Product higherRatedProduct;

    private double product1Score;
    private double product2Score;

    private String comparisonSummary;

    // Default constructor
    public ProductComparisonResponse() {
    }

    // Constructor for basic detailed comparison
    public ProductComparisonResponse(
            Product product1,
            Product product2,
            double priceDifference,
            Product cheaperProduct,
            double ratingDifference,
            Product higherRatedProduct) {

        this.product1 = product1;
        this.product2 = product2;
        this.priceDifference = priceDifference;
        this.cheaperProduct = cheaperProduct;
        this.ratingDifference = ratingDifference;
        this.higherRatedProduct = higherRatedProduct;
    }

    // Constructor for complete detailed comparison
    public ProductComparisonResponse(
            Product product1,
            Product product2,
            double priceDifference,
            Product cheaperProduct,
            double ratingDifference,
            Product higherRatedProduct,
            double product1Score,
            double product2Score,
            String comparisonSummary) {

        this.product1 = product1;
        this.product2 = product2;
        this.priceDifference = priceDifference;
        this.cheaperProduct = cheaperProduct;
        this.ratingDifference = ratingDifference;
        this.higherRatedProduct = higherRatedProduct;
        this.product1Score = product1Score;
        this.product2Score = product2Score;
        this.comparisonSummary = comparisonSummary;
    }

    public Product getProduct1() {
        return product1;
    }

    public void setProduct1(Product product1) {
        this.product1 = product1;
    }

    public Product getProduct2() {
        return product2;
    }

    public void setProduct2(Product product2) {
        this.product2 = product2;
    }

    public double getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(double priceDifference) {
        this.priceDifference = priceDifference;
    }

    public Product getCheaperProduct() {
        return cheaperProduct;
    }

    public void setCheaperProduct(Product cheaperProduct) {
        this.cheaperProduct = cheaperProduct;
    }

    public double getRatingDifference() {
        return ratingDifference;
    }

    public void setRatingDifference(double ratingDifference) {
        this.ratingDifference = ratingDifference;
    }

    public Product getHigherRatedProduct() {
        return higherRatedProduct;
    }

    public void setHigherRatedProduct(Product higherRatedProduct) {
        this.higherRatedProduct = higherRatedProduct;
    }

    public double getProduct1Score() {
        return product1Score;
    }

    public void setProduct1Score(double product1Score) {
        this.product1Score = product1Score;
    }

    public double getProduct2Score() {
        return product2Score;
    }

    public void setProduct2Score(double product2Score) {
        this.product2Score = product2Score;
    }

    public String getComparisonSummary() {
        return comparisonSummary;
    }

    public void setComparisonSummary(String comparisonSummary) {
        this.comparisonSummary = comparisonSummary;
    }
}
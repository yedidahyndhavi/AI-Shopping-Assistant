package com.aishopping.shoppingassistant.model;

public class ProductComparisonResponse {

    private Product product1;
    private Product product2;

    private double priceDifference;
    private String cheaperProduct;

    private double ratingDifference;
    private String higherRatedProduct;

    public ProductComparisonResponse() {
    }

    public ProductComparisonResponse(
            Product product1,
            Product product2,
            double priceDifference,
            String cheaperProduct,
            double ratingDifference,
            String higherRatedProduct) {

        this.product1 = product1;
        this.product2 = product2;
        this.priceDifference = priceDifference;
        this.cheaperProduct = cheaperProduct;
        this.ratingDifference = ratingDifference;
        this.higherRatedProduct = higherRatedProduct;
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

    public String getCheaperProduct() {
        return cheaperProduct;
    }

    public void setCheaperProduct(String cheaperProduct) {
        this.cheaperProduct = cheaperProduct;
    }

    public double getRatingDifference() {
        return ratingDifference;
    }

    public void setRatingDifference(double ratingDifference) {
        this.ratingDifference = ratingDifference;
    }

    public String getHigherRatedProduct() {
        return higherRatedProduct;
    }

    public void setHigherRatedProduct(String higherRatedProduct) {
        this.higherRatedProduct = higherRatedProduct;
    }
}

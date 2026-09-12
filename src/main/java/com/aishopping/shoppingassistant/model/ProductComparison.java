package com.aishopping.shoppingassistant.model;

public class ProductComparison {

    private Product product1;
    private Product product2;
    private double priceDifference;
    private String cheaperProduct;

    public ProductComparison() {
    }

    public ProductComparison(Product product1, Product product2,
                             double priceDifference, String cheaperProduct) {
        this.product1 = product1;
        this.product2 = product2;
        this.priceDifference = priceDifference;
        this.cheaperProduct = cheaperProduct;
    }

    public Product getProduct1() {
        return product1;
    }

    public Product getProduct2() {
        return product2;
    }

    public double getPriceDifference() {
        return priceDifference;
    }

    public String getCheaperProduct() {
        return cheaperProduct;
    }
}
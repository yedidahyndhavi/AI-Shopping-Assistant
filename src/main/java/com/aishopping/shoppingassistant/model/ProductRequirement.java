package com.aishopping.shoppingassistant.model;

public class ProductRequirement {

    private String category;

    private Double maxPrice;

    private Double minRating;

    private Double minRam;

    private Double minStorage;

    private Double minCamera;

    private Double minBattery;

    private Double minDisplaySize;

    private Double minRefreshRate;

    private Boolean requires5G;

    private String preferredBrand;


    // ============================================================
    // Default Constructor
    // ============================================================

    public ProductRequirement() {
    }


    // ============================================================
    // Category
    // ============================================================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    // ============================================================
    // Maximum Price
    // ============================================================

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }


    // ============================================================
    // Minimum Rating
    // ============================================================

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }


    // ============================================================
    // Minimum RAM
    // ============================================================

    public Double getMinRam() {
        return minRam;
    }

    public void setMinRam(Double minRam) {
        this.minRam = minRam;
    }


    // ============================================================
    // Minimum Storage
    // ============================================================

    public Double getMinStorage() {
        return minStorage;
    }

    public void setMinStorage(Double minStorage) {
        this.minStorage = minStorage;
    }


    // ============================================================
    // Minimum Camera
    // ============================================================

    public Double getMinCamera() {
        return minCamera;
    }

    public void setMinCamera(Double minCamera) {
        this.minCamera = minCamera;
    }


    // ============================================================
    // Minimum Battery
    // ============================================================

    public Double getMinBattery() {
        return minBattery;
    }

    public void setMinBattery(Double minBattery) {
        this.minBattery = minBattery;
    }


    // ============================================================
    // Minimum Display Size
    // ============================================================

    public Double getMinDisplaySize() {
        return minDisplaySize;
    }

    public void setMinDisplaySize(Double minDisplaySize) {
        this.minDisplaySize = minDisplaySize;
    }


    // ============================================================
    // Minimum Refresh Rate
    // ============================================================

    public Double getMinRefreshRate() {
        return minRefreshRate;
    }

    public void setMinRefreshRate(Double minRefreshRate) {
        this.minRefreshRate = minRefreshRate;
    }


    // ============================================================
    // 5G Requirement
    // ============================================================

    public Boolean getRequires5G() {
        return requires5G;
    }

    public void setRequires5G(Boolean requires5G) {
        this.requires5G = requires5G;
    }


    // ============================================================
    // Preferred Brand
    // ============================================================

    public String getPreferredBrand() {
        return preferredBrand;
    }

    public void setPreferredBrand(String preferredBrand) {
        this.preferredBrand = preferredBrand;
    }
}
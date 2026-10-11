
package com.aishopping.shoppingassistant.model;

public class ProductRequirement {

    private String category;
    private Double maxPrice;
    private Double minRating;

    // Smartphone requirements
    private Double minRam;
    private Double minStorage;
    private Double minCamera;
    private Double minBattery;
    private Double minDisplaySize;
    private Double minRefreshRate;
    private Boolean requires5G;

    // Laptop requirement
    private String preferredProcessor;

    // Smart TV requirements
    private String requiredResolution;
    private String preferredDisplayTechnology;

    // Headphones requirements
    private Boolean requiresNoiseCancellation;
    private Double minBatteryLife;
    private Double maxWeight;

    // Smartwatch requirements
    private Double minWaterResistance;
    private String requiredWaterResistance;
    private String preferredCompatibility;

    // Shared preference
    private String preferredBrand;

    public ProductRequirement() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }

    public Double getMinRam() {
        return minRam;
    }

    public void setMinRam(Double minRam) {
        this.minRam = minRam;
    }

    public Double getMinStorage() {
        return minStorage;
    }

    public void setMinStorage(Double minStorage) {
        this.minStorage = minStorage;
    }

    public Double getMinCamera() {
        return minCamera;
    }

    public void setMinCamera(Double minCamera) {
        this.minCamera = minCamera;
    }

    public Double getMinBattery() {
        return minBattery;
    }

    public void setMinBattery(Double minBattery) {
        this.minBattery = minBattery;
    }

    public Double getMinDisplaySize() {
        return minDisplaySize;
    }

    public void setMinDisplaySize(Double minDisplaySize) {
        this.minDisplaySize = minDisplaySize;
    }

    public Double getMinRefreshRate() {
        return minRefreshRate;
    }

    public void setMinRefreshRate(Double minRefreshRate) {
        this.minRefreshRate = minRefreshRate;
    }

    public Boolean getRequires5G() {
        return requires5G;
    }

    public void setRequires5G(Boolean requires5G) {
        this.requires5G = requires5G;
    }

    public String getPreferredProcessor() {
        return preferredProcessor;
    }

    public void setPreferredProcessor(String preferredProcessor) {
        this.preferredProcessor = preferredProcessor;
    }

    public String getRequiredResolution() {
        return requiredResolution;
    }

    public void setRequiredResolution(String requiredResolution) {
        this.requiredResolution = requiredResolution;
    }

    public String getPreferredDisplayTechnology() {
        return preferredDisplayTechnology;
    }

    public void setPreferredDisplayTechnology(
            String preferredDisplayTechnology) {
        this.preferredDisplayTechnology = preferredDisplayTechnology;
    }

    public Boolean getRequiresNoiseCancellation() {
        return requiresNoiseCancellation;
    }

    public void setRequiresNoiseCancellation(
            Boolean requiresNoiseCancellation) {
        this.requiresNoiseCancellation = requiresNoiseCancellation;
    }

    public Double getMinBatteryLife() {
        return minBatteryLife;
    }

    public void setMinBatteryLife(Double minBatteryLife) {
        this.minBatteryLife = minBatteryLife;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getMinWaterResistance() {
        return minWaterResistance;
    }

    public void setMinWaterResistance(Double minWaterResistance) {
        this.minWaterResistance = minWaterResistance;
    }

    public String getRequiredWaterResistance() {
        return requiredWaterResistance;
    }

    public void setRequiredWaterResistance(
            String requiredWaterResistance) {
        this.requiredWaterResistance = requiredWaterResistance;
    }

    public String getPreferredCompatibility() {
        return preferredCompatibility;
    }

    public void setPreferredCompatibility(String preferredCompatibility) {
        this.preferredCompatibility = preferredCompatibility;
    }

    public String getPreferredBrand() {
        return preferredBrand;
    }

    public void setPreferredBrand(String preferredBrand) {
        this.preferredBrand = preferredBrand;
    }
}

package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.ProductRequirement;
import com.aishopping.shoppingassistant.model.RecommendationRequest;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NaturalLanguageQueryService {

    // ============================================================
    // Existing query parser
    // ============================================================

    public RecommendationRequest parseQuery(String query) {

        String category =
                extractCategory(query);

        double maxPrice =
                extractMaxPrice(query);

        double minRating =
                extractMinRating(query);

        return new RecommendationRequest(
                category,
                maxPrice,
                minRating
        );
    }


    // ============================================================
    // Structured requirement parser
    // ============================================================

    public ProductRequirement parseRequirements(
            String query) {

        ProductRequirement requirement =
                new ProductRequirement();

        requirement.setCategory(
                extractCategory(query));

        requirement.setMaxPrice(
                extractMaxPrice(query));

        requirement.setMinRating(
                extractMinRating(query));

        requirement.setMinRam(
                extractRam(query));

        requirement.setMinStorage(
                extractStorage(query));

        requirement.setMinCamera(
                extractCamera(query));

        requirement.setMinBattery(
                extractBattery(query));

        requirement.setMinDisplaySize(
                extractDisplaySize(query));

        requirement.setMinRefreshRate(
                extractRefreshRate(query));

        requirement.setRequires5G(
                extract5G(query));

        requirement.setPreferredBrand(
                extractPreferredBrand(query));

        return requirement;
    }


    // ============================================================
    // Extract category
    // ============================================================

    private String extractCategory(
            String query) {

        if (query == null) {
            return "";
        }

        String lowerQuery =
                query.toLowerCase();

        if (lowerQuery.contains("smartphone")
                || lowerQuery.contains("phone")) {

            return "Smartphone";
        }

        if (lowerQuery.contains("laptop")
                || lowerQuery.contains("macbook")) {

            return "Laptop";
        }

        return "";
    }


    // ============================================================
    // Extract maximum price
    // ============================================================

    private double extractMaxPrice(
            String query) {

        if (query == null) {
            return Double.MAX_VALUE;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:under|below|less than|maximum|max)"
                                + "\\s*"
                                + "(?:₹|rs\\.?|inr)?"
                                + "\\s*"
                                + "(\\d+(?:,\\d+)*)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
                            .replace(",", "")
            );
        }

        return Double.MAX_VALUE;
    }


    // ============================================================
    // Extract minimum rating
    // ============================================================

    private double extractMinRating(
            String query) {

        if (query == null) {
            return 0.0;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:rating|rated)"
                                + "\\s*"
                                + "(?:above|over|greater than|at least)?"
                                + "\\s*"
                                + "(\\d+(?:\\.\\d+)?)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return 0.0;
    }


    // ============================================================
    // Extract RAM requirement
    // ============================================================

    private Double extractRam(
            String query) {

        if (query == null) {
            return null;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:at least|minimum|min)?"
                                + "\\s*"
                                + "(\\d+(?:\\.\\d+)?)"
                                + "\\s*(?:gb|g)"
                                + "\\s*(?:ram|memory)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ============================================================
    // Extract storage requirement
    // ============================================================

    private Double extractStorage(
            String query) {

        if (query == null) {
            return null;
        }

        /*
         * Storage must explicitly be followed by:
         *
         * storage
         * ROM
         * SSD
         * disk
         *
         * This prevents:
         *
         * 12GB RAM
         *
         * from being incorrectly detected as
         * 12GB storage.
         */

        Pattern pattern =
                Pattern.compile(
                        "(\\d+(?:\\.\\d+)?)"
                                + "\\s*(gb|tb)"
                                + "\\s*(?:storage|rom|ssd|disk)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            double value =
                    Double.parseDouble(
                            matcher.group(1)
                    );

            String unit =
                    matcher.group(2)
                            .toLowerCase();

            // Convert TB to GB
            if (unit.equals("tb")) {

                value = value * 1024;
            }

            return value;
        }

        return null;
    }


    // ============================================================
    // Extract camera requirement
    // ============================================================

    private Double extractCamera(
            String query) {

        if (query == null) {
            return null;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:at least|minimum|min)?"
                                + "\\s*"
                                + "(\\d+(?:\\.\\d+)?)"
                                + "\\s*(?:mp|megapixel)"
                                + "\\s*(?:camera)?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ============================================================
    // Extract battery requirement
    // ============================================================

    private Double extractBattery(
            String query) {

        if (query == null) {
            return null;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:at least|minimum|min)?"
                                + "\\s*"
                                + "(\\d+(?:\\.\\d+)?)"
                                + "\\s*(?:mah|mAh)"
                                + "\\s*(?:battery)?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ============================================================
    // Extract display size
    // ============================================================

    private Double extractDisplaySize(
            String query) {

        if (query == null) {
            return null;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:at least|minimum|min)?"
                                + "\\s*"
                                + "(\\d+(?:\\.\\d+)?)"
                                + "\\s*(?:inch|inches|in|\\\")"
                                + "\\s*(?:display|screen)?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ============================================================
    // Extract refresh rate
    // ============================================================

    private Double extractRefreshRate(
            String query) {

        if (query == null) {
            return null;
        }

        Pattern pattern =
                Pattern.compile(
                        "(?:at least|minimum|min)?"
                                + "\\s*"
                                + "(\\d+(?:\\.\\d+)?)"
                                + "\\s*(?:hz)"
                                + "\\s*(?:refresh rate)?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(query);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ============================================================
    // Extract 5G requirement
    // ============================================================

    private Boolean extract5G(
            String query) {

        if (query == null) {
            return null;
        }

        String lowerQuery =
                query.toLowerCase();

        if (lowerQuery.contains("5g")) {

            return true;
        }

        return null;
    }


    // ============================================================
    // Extract preferred brand
    // ============================================================

    private String extractPreferredBrand(
            String query) {

        if (query == null) {
            return null;
        }

        String lowerQuery =
                query.toLowerCase();

        String[] brands = {
                "apple",
                "samsung",
                "google",
                "oneplus",
                "dell",
                "asus",
                "lenovo"
        };

        for (String brand : brands) {

            if (lowerQuery.contains(brand)) {

                return capitalizeBrand(brand);
            }
        }

        return null;
    }


    // ============================================================
    // Format brand name
    // ============================================================

    private String capitalizeBrand(
            String brand) {

        if (brand == null
                || brand.isEmpty()) {

            return brand;
        }

        if (brand.equalsIgnoreCase("oneplus")) {
            return "OnePlus";
        }

        if (brand.equalsIgnoreCase("asus")) {
            return "ASUS";
        }

        if (brand.equalsIgnoreCase("dell")) {
            return "Dell";
        }

        if (brand.equalsIgnoreCase("lenovo")) {
            return "Lenovo";
        }

        if (brand.equalsIgnoreCase("apple")) {
            return "Apple";
        }

        if (brand.equalsIgnoreCase("samsung")) {
            return "Samsung";
        }

        if (brand.equalsIgnoreCase("google")) {
            return "Google";
        }

        return brand;
    }
}
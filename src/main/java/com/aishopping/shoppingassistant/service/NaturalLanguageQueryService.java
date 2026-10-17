
package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.ProductRequirement;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NaturalLanguageQueryService {

    // ============================================================
    // EXISTING QUERY PARSER
    // ============================================================

    public RecommendationRequest parseQuery(String query) {
        String text = safeQuery(query);

        return new RecommendationRequest(
                extractCategory(text),
                extractMaxPrice(text),
                extractMinRating(text)
        );
    }

    // ============================================================
    // STRUCTURED REQUIREMENT PARSER
    // ============================================================

    
public ProductRequirement parseRequirements(String query) {
    String text = safeQuery(query);
    ProductRequirement requirement = new ProductRequirement();

    
requirement.setCategory(extractCategory(text));
requirement.setMaxPrice(extractMaxPrice(text));
requirement.setMinRating(extractMinRating(text));

requirement.setStrictMinRating(
        text.matches("(?i).*\\b(above|over|greater than)\\b.*")
);

    System.out.println("DEBUG minRating = "
            + requirement.getMinRating());
    System.out.println("DEBUG strictMinRating = "
            + requirement.isStrictMinRating());

    // Smartphone requirements
    requirement.setMinRam(extractRam(text));
    requirement.setMinStorage(extractStorage(text));
    requirement.setMinCamera(extractCamera(text));
    requirement.setMinBattery(extractBattery(text));
    requirement.setMinDisplaySize(extractDisplaySize(text));
    requirement.setMinRefreshRate(extractRefreshRate(text));
    requirement.setRequires5G(extract5G(text));

    // Shared and other product requirements
    requirement.setPreferredBrand(extractPreferredBrand(text));

    return requirement;
}


    private String safeQuery(String query) {
        return query == null ? "" : query.trim();
    }

    // ============================================================
    // CATEGORY DETECTION
    // ============================================================

    private String extractCategory(String query) {
        String text = query.toLowerCase(Locale.ROOT);

        // Check specific categories before generic keywords.
        if (containsAny(text, "smartwatch", "smart watch",
                "fitness watch", "fitness tracker", "wearable")) {
            return "Smartwatch";
        }

        if (containsAny(text, "headphones", "headphone",
                "earbuds", "earbud", "earphones", "earphone",
                "wireless buds", "headset")) {
            return "Headphones";
        }

        if (containsAny(text, "smart tv", "smart television",
                "television", "tv")) {
            return "TV";
        }

        if (containsAny(text, "smartphone", "smart phone",
                "mobile phone", "mobile", "phone")) {
            return "Smartphone";
        }

        if (containsAny(text, "laptop", "macbook",
                "notebook computer")) {
            return "Laptop";
        }

        return "";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile(
                    "(?<![a-z0-9])" + Pattern.quote(keyword)
                            + "(?![a-z0-9])",
                    Pattern.CASE_INSENSITIVE
            );

            if (pattern.matcher(text).find()) {
                return true;
            }
        }

        return false;
    }

    // ============================================================
    // PRICE
    // ============================================================

    private double extractMaxPrice(String query) {
        Pattern pattern = Pattern.compile(
                "(?:under|below|less than|maximum|max|within|"
                        + "budget(?: of)?|up to)\\s*"
                        + "(?:₹|rs\\.?\\s*|inr\\s*)?"
                        + "(\\d[\\d,]*(?:\\.\\d+)?)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return Double.parseDouble(
                    matcher.group(1).replace(",", ""));
        }

        return Double.MAX_VALUE;
    }

    // ============================================================
    // MINIMUM RATING
    // ============================================================

    
private double extractMinRating(String query) {
    Pattern pattern = Pattern.compile(
            "(?:rating|rated)\\s*(?:of\\s*)?"
                    + "(?:(?:above|over|greater than|at least)\\s*)?"
                    + "(\\d+(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE
    );

    Matcher matcher = pattern.matcher(query);

    if (matcher.find()) {
        return Double.parseDouble(matcher.group(1));
    }

    return 0.0;
}




    // ============================================================
    // SMARTPHONE REQUIREMENTS
    // ============================================================

    private Double extractRam(String query) {
        Pattern pattern = Pattern.compile(
                "(?:at least|minimum|min)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*(?:gb|g)\\s*"
                        + "(?:ram|memory)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        return matcher.find()
                ? Double.parseDouble(matcher.group(1))
                : null;
    }

    private Double extractStorage(String query) {
        Pattern pattern = Pattern.compile(
                "(\\d+(?:\\.\\d+)?)\\s*(gb|tb)\\s*"
                        + "(?:storage|rom|ssd|disk)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return null;
        }

        double value = Double.parseDouble(matcher.group(1));

        if (matcher.group(2).equalsIgnoreCase("tb")) {
            value *= 1024;
        }

        return value;
    }

    private Double extractCamera(String query) {
        Pattern pattern = Pattern.compile(
                "(?:at least|minimum|min)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*"
                        + "(?:mp|megapixels?|megapixel)"
                        + "\\s*(?:camera)?",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        return matcher.find()
                ? Double.parseDouble(matcher.group(1))
                : null;
    }

    private Double extractBattery(String query) {
        Pattern pattern = Pattern.compile(
                "(?:at least|minimum|min)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*"
                        + "(?:mah|milliamp.hours?)\\s*(?:battery)?",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        return matcher.find()
                ? Double.parseDouble(matcher.group(1))
                : null;
    }

    private Double extractDisplaySize(String query) {
        Pattern pattern = Pattern.compile(
                "(?:at least|minimum|min|larger than|over)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*"
                        + "(?:inches|inch|in)\\b\\s*"
                        + "(?:display|screen)?",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }

        return null;
    }

    private Double extractRefreshRate(String query) {
        Pattern pattern = Pattern.compile(
                "(?:at least|minimum|min)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*hz",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        return matcher.find()
                ? Double.parseDouble(matcher.group(1))
                : null;
    }

    private Boolean extract5G(String query) {
        Pattern pattern = Pattern.compile("\\b5g\\b",
                Pattern.CASE_INSENSITIVE);

        return pattern.matcher(query).find() ? true : null;
    }

    // ============================================================
    // LAPTOP PROCESSOR REQUIREMENT
    // ============================================================

    private String extractProcessor(String query) {
        Pattern pattern = Pattern.compile(
                "\\b(?:intel\\s+core\\s+)?"
                        + "(i[3579]|m[1-4]|ryzen\\s*[3579])\\b",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return null;
        }

        return matcher.group(1).replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    // ============================================================
    // SMART TV REQUIREMENTS
    // ============================================================

    private String extractResolution(String query) {
        String text = query.toLowerCase(Locale.ROOT);

        if (containsAny(text, "8k", "4320p")) {
            return "8K";
        }

        if (containsAny(text, "4k", "2160p", "uhd", "ultra hd")) {
            return "4K";
        }

        if (containsAny(text, "1080p", "full hd", "fhd")) {
            return "Full HD";
        }

        if (containsAny(text, "720p", "hd ready")) {
            return "HD";
        }

        return null;
    }

    private String extractDisplayTechnology(String query) {
        String text = query.toLowerCase(Locale.ROOT);

        if (containsAny(text, "mini led", "mini-led")) {
            return "Mini LED";
        }

        if (containsAny(text, "oled")) {
            return "OLED";
        }

        if (containsAny(text, "qled")) {
            return "QLED";
        }

        if (containsAny(text, "led")) {
            return "LED";
        }

        return null;
    }

    // ============================================================
    // HEADPHONES REQUIREMENTS
    // ============================================================

    private Boolean extractNoiseCancellation(String query) {
        String text = query.toLowerCase(Locale.ROOT);

        if (containsAny(text, "without noise cancellation",
                "no noise cancellation",
                "noise cancellation not required")) {
            return false;
        }

        if (containsAny(text, "noise cancellation",
                "noise cancelling", "noise-cancelling",
                "active noise control", "anc")) {
            return true;
        }

        return null;
    }

    /*
     * Returns battery life in HOURS.
     * Examples: "20 hours battery life" -> 20
     *           "2 days battery life"   -> 48
     */
    private Double extractBatteryLifeHours(String query) {
        Pattern pattern = Pattern.compile(
                "(?:battery life|playback|runtime)\\s*(?:of)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*"
                        + "(hours?|hrs?|days?)\\b"
                        + "|(?:at least|minimum|min|over|above|more than)?\\s*"
                        + "(\\d+(?:\\.\\d+)?)\\s*"
                        + "(hours?|hrs?|days?)\\b"
                        + "\\s*(?:of\\s*)?(?:battery life|playback|runtime)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return null;
        }

        String number = matcher.group(1) != null
                ? matcher.group(1) : matcher.group(3);

        String unit = matcher.group(2) != null
                ? matcher.group(2) : matcher.group(4);

        double value = Double.parseDouble(number);

        if (unit.toLowerCase(Locale.ROOT).startsWith("day")) {
            value *= 24;
        }

        return value;
    }

    /*
     * Returns maximum weight in GRAMS.
     */
    private Double extractMaxWeightGrams(String query) {
        Pattern pattern = Pattern.compile(
                "(?:weight\\s*(?:under|below|less than|max)?\\s*"
                        + "|(?:under|below|less than|max)\\s*)"
                        + "(\\d+(?:\\.\\d+)?)\\s*"
                        + "(kg|kgs|kilograms?|g|grams?)\\b",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return null;
        }

        double value = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toLowerCase(Locale.ROOT);

        if (unit.startsWith("kg") || unit.startsWith("kilogram")) {
            value *= 1000;
        }

        return value;
    }

    // ============================================================
    // SMARTWATCH REQUIREMENTS
    // ============================================================

    private String extractWaterResistance(String query) {
        String text = query.toLowerCase(Locale.ROOT);

        if (containsAny(text, "10 atm")) {
            return "10 ATM";
        }

        if (containsAny(text, "5 atm")) {
            return "5 ATM";
        }

        if (containsAny(text, "3 atm")) {
            return "3 ATM";
        }

        if (containsAny(text, "ip68")) {
            return "IP68";
        }

        if (containsAny(text, "ip67")) {
            return "IP67";
        }

        if (containsAny(text, "water resistant",
                "water resistance", "waterproof", "swimming")) {
            return "Water Resistant";
        }

        return null;
    }

    private Double extractWaterResistanceLevel(String query) {
        Pattern pattern = Pattern.compile(
                "\\b(3|5|10)\\s*atm\\b",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(query);

        return matcher.find()
                ? Double.parseDouble(matcher.group(1))
                : null;
    }

    private String extractCompatibility(String query) {
        String text = query.toLowerCase(Locale.ROOT);

        if (containsAny(text, "ios", "iphone compatible",
                "apple compatible")) {
            return "iOS";
        }

        if (containsAny(text, "android compatible", "android phone")) {
            return "Android";
        }

        if (containsAny(text, "android", "ios")) {
            return "Android/iOS";
        }

        return null;
    }

    // ============================================================
    // PREFERRED BRAND
    // ============================================================

    private String extractPreferredBrand(String query) {
        String[] brands = {
                "oneplus", "samsung", "google", "apple",
                "asus", "dell", "lenovo", "sony", "lg",
                "xiaomi", "redmi", "realme", "boat",
                "jbl", "bose", "noise", "amazfit",
                "garmin", "fitbit", "tcl", "hisense",
                "motorola", "hp", "acer", "msi"
        };

        for (String brand : brands) {
            if (containsAny(query, brand)) {
                return capitalizeBrand(brand);
            }
        }

        return null;
    }

    private String capitalizeBrand(String brand) {
        switch (brand.toLowerCase(Locale.ROOT)) {
            case "oneplus":
                return "OnePlus";
            case "asus":
                return "ASUS";
            case "lg":
                return "LG";
            case "jbl":
                return "JBL";
            case "hp":
                return "HP";
            case "msi":
                return "MSI";
            case "boat":
                return "boAt";
            case "xiaomi":
                return "Xiaomi";
            case "redmi":
                return "Redmi";
            case "realme":
                return "Realme";
            case "tcl":
                return "TCL";
            default:
                return brand.substring(0, 1).toUpperCase(Locale.ROOT)
                        + brand.substring(1).toLowerCase(Locale.ROOT);
        }
    }
}

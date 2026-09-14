package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.RecommendationRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {

    private final ProductService productService;
    private final ProductRankingService productRankingService;

    public ProductRecommendationService(
            ProductService productService,
            ProductRankingService productRankingService) {

        this.productService = productService;
        this.productRankingService = productRankingService;
    }

    public Product recommendBestProduct(RecommendationRequest request) {

        List<Product> products = productService.getAllProducts();

        List<Product> matchingProducts = products.stream()
                .filter(product ->
                        product.getCategory().equalsIgnoreCase(request.getCategory()))
                .filter(product ->
                        product.getPrice() <= request.getMaxPrice())
                .filter(product ->
                        product.getRating() >= request.getMinRating())
                .collect(Collectors.toList());

        if (matchingProducts.isEmpty()) {
            throw new RuntimeException(
                    "No products match the given preferences"
            );
        }

        List<Product> rankedProducts =
                productRankingService.rankProducts(matchingProducts);

        return rankedProducts.get(0);
    }
}
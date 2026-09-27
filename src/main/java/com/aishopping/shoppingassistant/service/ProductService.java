package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.repository.ProductRepository;
import org.springframework.stereotype.Service;
import com.aishopping.shoppingassistant.model.ProductComparisonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.aishopping.shoppingassistant.service.ProductEvaluationService;

import java.util.List;

@Service
public class ProductService {

   private final ProductRepository productRepository;
private final ProductEvaluationService productEvaluationService;
    

    public ProductService(
        ProductRepository productRepository,
        ProductEvaluationService productEvaluationService) {

    this.productRepository = productRepository;
    this.productEvaluationService = productEvaluationService;
}

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {

    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

    existingProduct.setName(updatedProduct.getName());
    existingProduct.setBrand(updatedProduct.getBrand());
    existingProduct.setPrice(updatedProduct.getPrice());
    existingProduct.setCategory(updatedProduct.getCategory());
    existingProduct.setRating(updatedProduct.getRating());
    existingProduct.setDescription(updatedProduct.getDescription());

    return productRepository.save(existingProduct);
}

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    public List<Product> filterByCategory(String category) {
    return productRepository.findByCategoryIgnoreCase(category);
}

    public List<Product> filterByMaxPrice(double price) {
    return productRepository.findByPriceLessThanEqual(price);
}

    public List<Product> filterByMinRating(double rating) {
    return productRepository.findByRatingGreaterThanEqual(rating);
}

    public ProductComparison compareProducts(List<Long> ids) {

        List<Product> products = productRepository.findAllById(ids);

        if (products.size() != 2) {
            throw new IllegalArgumentException("Exactly two products are required for comparison");
        }

        Product product1 = products.get(0);
        Product product2 = products.get(1);

        double priceDifference =
                Math.abs(product1.getPrice() - product2.getPrice());

        String cheaperProduct;

        if (product1.getPrice() < product2.getPrice()) {
            cheaperProduct = product1.getName();
        } else if (product2.getPrice() < product1.getPrice()) {
            cheaperProduct = product2.getName();
        } else {
            cheaperProduct = "Same price";
        }

        return new ProductComparison(
                product1,
                product2,
                priceDifference,
                cheaperProduct
        );
    }
    public ProductComparisonResponse compareProductsDetailed(List<Long> ids) {

    List<Product> products = productRepository.findAllById(ids);

    if (products.size() != 2) {
        throw new IllegalArgumentException(
                "Exactly two products are required for comparison");
    }

    Product product1 = products.get(0);
    Product product2 = products.get(1);
    double product1Score =
        productEvaluationService.calculateScore(product1);

double product2Score =
        productEvaluationService.calculateScore(product2);
        String comparisonSummary;

if (product1Score > product2Score) {

    comparisonSummary =
            product1.getName()
            + " has a higher overall score than "
            + product2.getName();

} else if (product2Score > product1Score) {

    comparisonSummary =
            product2.getName()
            + " has a higher overall score than "
            + product1.getName();

} else {

    comparisonSummary =
            "Both products have the same overall score";
}

    double priceDifference =
            Math.abs(product1.getPrice() - product2.getPrice());

    String cheaperProduct;

    if (product1.getPrice() < product2.getPrice()) {
        cheaperProduct = product1.getName();
    } else if (product2.getPrice() < product1.getPrice()) {
        cheaperProduct = product2.getName();
    } else {
        cheaperProduct = "Same price";
    }

    double ratingDifference =
            Math.abs(product1.getRating() - product2.getRating());

    String higherRatedProduct;

    if (product1.getRating() > product2.getRating()) {
        higherRatedProduct = product1.getName();
    } else if (product2.getRating() > product1.getRating()) {
        higherRatedProduct = product2.getName();
    } else {
        higherRatedProduct = "Same rating";
    }

    return new ProductComparisonResponse(
        product1,
        product2,
        priceDifference,
        cheaperProduct,
        ratingDifference,
        higherRatedProduct,
        product1Score,
        product2Score,
        comparisonSummary
);
}
}
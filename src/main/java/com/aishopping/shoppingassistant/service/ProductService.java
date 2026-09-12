package com.aishopping.shoppingassistant.service;

import com.aishopping.shoppingassistant.model.Product;
import com.aishopping.shoppingassistant.model.ProductComparison;
import com.aishopping.shoppingassistant.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
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
}
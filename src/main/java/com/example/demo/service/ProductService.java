package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Create
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Read All
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Read by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Update
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product productToUpdate = product.get();
            productToUpdate.setName(productDetails.getName());
            productToUpdate.setDescription(productDetails.getDescription());
            productToUpdate.setPrice(productDetails.getPrice());
            productToUpdate.setQuantity(productDetails.getQuantity());
            return productRepository.save(productToUpdate);
        }
        return null;
    }

    // Delete
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

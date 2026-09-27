package com.akhil.ecommerceapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.akhil.ecommerceapi.entity.Product;
import com.akhil.ecommerceapi.exception.AiServiceUnavailableException;
import com.akhil.ecommerceapi.exception.ProductNotFoundException;
import com.akhil.ecommerceapi.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public ProductService(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }
    
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }
    
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setCategory(updatedProduct.getCategory());

        return productRepository.save(existingProduct);
    }
    
//    public void deleteProduct(Long id) {
//    	Product existingProduct = getProductById(id);
//    	
//    	productRepository.delete(existingProduct);
//    }/
    
    public void deleteProduct(Long id) {
    	Product product = getProductById(id);
    	product.setActive(false);
    	productRepository.save(product);
    }
    
    public Product generateDescription(Long productId) {
        Product product = getProductById(productId);

        Map<String, String> request = Map.of(
                "productName", product.getName(),
                "category", product.getCategory(),
                "existingDetails", product.getDescription() != null ? product.getDescription() : ""
        );

        try {
            Map<String, String> response = restTemplate.postForObject(
                    "http://localhost:8081/api/ai/generate-description",
                    request,
                    Map.class
            );
            product.setDescription(response.get("description"));
            return productRepository.save(product);
        } catch (RestClientException e) {
            throw new AiServiceUnavailableException("AI description service is currently unavailable. Please try again later.");
        }
    }

}
package com.shop.product_service.service;

import com.shop.product_service.exception.NotFoundException;
import com.shop.product_service.model.Product;
import com.shop.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repo;

    @Autowired
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product create(Product p) {
        return repo.save(p);
    }

    public Product findOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    public Page<Product> list(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (category == null || category.isBlank()) {
            return repo.findAll(pageable);
        } else {
            return repo.findByCategoryIgnoreCase(category, pageable);
        }
    }

    public Product update(Long id, Product updates) {
        Product existing = findOrThrow(id);
        // keep SKU unless explicitly changed
        if (updates.getSku() != null && !updates.getSku().isBlank()) {
            existing.setSku(updates.getSku());
        }
        existing.setName(updates.getName());
        existing.setDescription(updates.getDescription());
        existing.setPrice(updates.getPrice());
        existing.setCurrency(updates.getCurrency());
        existing.setCategory(updates.getCategory());
        return repo.save(existing);
    }

    public void delete(Long id) {
        Product p = findOrThrow(id);
        repo.delete(p);
    }
}

package com.shop.product_service.controller;

import org.springframework.web.server.ResponseStatusException;
import com.shop.product_service.dto.ProductDto;
import com.shop.product_service.model.Product;
import com.shop.product_service.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {

    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        // map
        Product p = toEntity(dto);

        // SKU uniqueness check
        service.list(null, 0, Integer.MAX_VALUE)
                .stream()
                .filter(pr -> pr.getSku().equalsIgnoreCase(p.getSku()))
                .findFirst()
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
                });

        Product saved = service.create(p);
        ProductDto resp = toDto(saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        Product p = service.findOrThrow(id);
        return toDto(p);
    }

    @GetMapping
    public Page<ProductDto> list(@RequestParam(required = false) String category,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        Page<Product> pageRes = service.list(category, page, size);
        return pageRes.map(this::toDto);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        Product updates = toEntity(dto);
        Product updated = service.update(id, updates);
        return toDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // mapping helpers
    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product p = new Product();
        BeanUtils.copyProperties(dto, p);
        return p;
    }
}

package com.onlineshop.productservice.service;

import java.util.List;

import com.onlineshop.productservice.dto.ProductRequestDTO;
import com.onlineshop.productservice.dto.ProductResponseDTO;
import com.onlineshop.productservice.model.Product;
import com.onlineshop.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequestDTO productRequestDTO){
        Product product = Product.builder()
            .name(productRequestDTO.getName())
            .description(productRequestDTO.getDescription())
            .price(productRequestDTO.getPrice())
            .build();
        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponseDto).toList();
    }

    private ProductResponseDTO mapToProductResponseDto(Product product) {
        return ProductResponseDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .build();
    }
}

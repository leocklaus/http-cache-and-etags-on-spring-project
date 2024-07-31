package io.github.leocklaus.cache_etag_demo.domain.service;

import io.github.leocklaus.cache_etag_demo.api.dto.ProductInput;
import io.github.leocklaus.cache_etag_demo.domain.entity.Product;
import io.github.leocklaus.cache_etag_demo.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAllProductsPaged(Pageable pageable){
        return productRepository.findAllProductsPaged(pageable);
    }

    @Transactional
    public Product saveProduct(ProductInput productInput){
        Product product = Product.builder()
                .name(productInput.name())
                .price(productInput.price())
                .build();

        return productRepository.save(product);
    }

    public LocalDateTime getLastUpdatedProduct(){
        return productRepository.lastProductUpdate();
    }

}

package io.github.leocklaus.cache_etag_demo.api.controllers;

import io.github.leocklaus.cache_etag_demo.api.dto.ProductInput;
import io.github.leocklaus.cache_etag_demo.domain.entity.Product;
import io.github.leocklaus.cache_etag_demo.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProductsPaged(Pageable pageable){
        Page<Product> products = productService.getAllProductsPaged(pageable);

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
                .body(products);

    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody ProductInput productInput){
        Product product = productService.saveProduct(productInput);
        URI uri = URI.create("/api/v1/product" + product.getId());
        return ResponseEntity
                .created(uri)
                .body(product);
    }

}

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
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProductsPaged(
            Pageable pageable,
            ServletWebRequest request){

        ShallowEtagHeaderFilter.disableContentCaching(request.getRequest());

        String ETag = getETag();

        if(request.checkNotModified(ETag)){
            return null;
        }

        Page<Product> products = productService.getAllProductsPaged(pageable);

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl
                        .maxAge(1, TimeUnit.MINUTES)
                        .cachePublic())
                .eTag(ETag)
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

    private String getETag() {
        String ETag = "0";

        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        ZoneOffset zoneOffset = zdt.getOffset();

        LocalDateTime lasCreatedProductDate = productService.getLastCreatedProduct();

        if(lasCreatedProductDate != null){
            ETag = String.valueOf(
                    lasCreatedProductDate.toEpochSecond(zoneOffset));
        }
        return ETag;
    }

}

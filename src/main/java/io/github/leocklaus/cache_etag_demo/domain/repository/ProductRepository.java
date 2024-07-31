package io.github.leocklaus.cache_etag_demo.domain.repository;

import io.github.leocklaus.cache_etag_demo.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT MAX(p.createdAt) FROM Product p")
    LocalDateTime lastProductCreatedDate();

    @Query("SELECT p FROM Product p")
    Page<Product> findAllProductsPaged(Pageable pageable);
}

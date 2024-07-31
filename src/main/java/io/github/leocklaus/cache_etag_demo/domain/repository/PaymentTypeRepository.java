package io.github.leocklaus.cache_etag_demo.domain.repository;

import io.github.leocklaus.cache_etag_demo.domain.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
}

package io.github.leocklaus.cache_etag_demo.api.dto;

import java.math.BigDecimal;

public record ProductInput(
        String name,
        BigDecimal price
) {
}

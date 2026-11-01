package com.myp.jobs.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Product(Long id,
                      String name,
                      String description,
                      BigDecimal price,
                      LocalDateTime createdAt,
                      ProductCategory category,
                      boolean active) {

    public static Product of(String name,
                             String description,
                             BigDecimal price,
                             ProductCategory category) {
        return new Product(null, name, description, price, LocalDateTime.now(), category, true);
    }

}

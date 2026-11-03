package com.myp.jobs.domain.services.jobs.load.csv;

import java.math.BigDecimal;

public record ProductCsv(String name,
                         String description,
                         BigDecimal price,
                         String category) {
}
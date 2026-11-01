package com.myp.jobs.domain.outbound;

import com.myp.jobs.domain.models.ProductCategory;

import java.util.Optional;

public interface ProductCategoryInventory {

    Optional<ProductCategory> findByCode(String code);
}

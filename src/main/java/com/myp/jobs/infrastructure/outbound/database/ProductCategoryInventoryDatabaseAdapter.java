package com.myp.jobs.infrastructure.outbound.database;

import com.myp.jobs.domain.models.ProductCategory;
import com.myp.jobs.domain.outbound.ProductCategoryInventory;
import com.myp.jobs.infrastructure.outbound.database.mappers.ProductCategoryMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductCategoryInventoryDatabaseAdapter implements ProductCategoryInventory {

    private final ProductCategoryMapper mapper;

    public ProductCategoryInventoryDatabaseAdapter(ProductCategoryMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductCategory> findByCode(String code) {
        ProductCategory category = mapper.findByCode(code);
        return Optional.ofNullable(category);
    }

}

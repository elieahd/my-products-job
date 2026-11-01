package com.myp.jobs.infrastructure.outbound.database;

import com.myp.jobs.domain.models.Product;
import com.myp.jobs.domain.outbound.ProductInventory;
import com.myp.jobs.infrastructure.outbound.database.mappers.ProductMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductInventoryDatabaseAdapter implements ProductInventory {

    private final ProductMapper mapper;

    public ProductInventoryDatabaseAdapter(ProductMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void createAll(List<Product> products) {
        mapper.createAll(products);
    }

}

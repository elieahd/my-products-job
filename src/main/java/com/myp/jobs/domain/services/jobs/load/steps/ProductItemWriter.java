package com.myp.jobs.domain.services.jobs.load.steps;

import com.myp.jobs.domain.models.Product;
import com.myp.jobs.domain.outbound.ProductInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProductItemWriter implements ItemWriter<Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductItemWriter.class);

    private final ProductInventory productInventory;

    public ProductItemWriter(ProductInventory productInventory) {
        this.productInventory = productInventory;
    }

    @Override
    public void write(Chunk<? extends Product> chunk) throws Exception {
        log.info("[Writer] Writing {} products...", chunk.getItems().size());
        productInventory.createAll(new ArrayList<>(chunk.getItems()));
    }
}
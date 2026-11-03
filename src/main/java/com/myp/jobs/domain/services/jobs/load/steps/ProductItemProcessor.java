package com.myp.jobs.domain.services.jobs.load.steps;

import com.myp.jobs.domain.models.Product;
import com.myp.jobs.domain.models.ProductCategory;
import com.myp.jobs.domain.outbound.ProductCategoryInventory;
import com.myp.jobs.domain.services.jobs.load.csv.ProductCsv;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductItemProcessor implements ItemProcessor<ProductCsv, Product> {

    private static final Logger log = LoggerFactory.getLogger(ProductItemProcessor.class);

    private final ProductCategoryInventory categoryInventory;

    public ProductItemProcessor(ProductCategoryInventory categoryInventory) {
        this.categoryInventory = categoryInventory;
    }

    @Override
    public @Nullable Product process(ProductCsv item) throws Exception {
        if (item.name() == null || item.name().isBlank()) {
            log.warn("[Processor] [Skipping] Product name is null or blank");
            return null;
        }

        if (item.description() == null || item.description().isBlank()) {
            log.warn("[Processor] [Skipping] Product name is null or blank");
            return null;
        }

        if (item.price() == null) {
            log.warn("[Processor] [Skipping] Product price is null");
            return null;
        }

        if (item.price().signum() < 0) {
            log.warn("[Processor] [Skipping] Product price is negative");
            return null;
        }

        if (item.category() == null || item.category().isBlank()) {
            log.warn("[Processor] [Skipping] Product category is null or blank");
            return null;
        }

        Optional<ProductCategory> productCategory = categoryInventory.findByCode(item.category());

        if (productCategory.isEmpty()) {
            log.warn("[Processor] [Skipping] Product Category %s is not found".formatted(item.category()));
            return null;
        }

        return Product.of(
                item.name(),
                item.description(),
                item.price(),
                productCategory.get()
        );
    }
}

package com.myp.jobs.domain.outbound;

import com.myp.jobs.domain.models.Product;

import java.util.List;

public interface ProductInventory {

    void createAll(List<Product> products);

}

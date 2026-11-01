package com.myp.jobs.infrastructure.outbound.database.mappers;

import com.myp.jobs.domain.models.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

    void createAll(List<Product> products);

}


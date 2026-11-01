package com.myp.jobs.infrastructure.outbound.database.mappers;

import com.myp.jobs.domain.models.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCategoryMapper {

    ProductCategory findByCode(String code);

}

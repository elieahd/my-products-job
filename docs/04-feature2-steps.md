For this feature, we will need to create the `exportProducts` Job (`ExportProductJob`) that reads products from the
database page by page using MyBatis, and writes them to a CSV file in chunks.

### Representation of the exported row

We will reuse the `ProductCsv` record from Feature 1. 

It is a plain data holder that mirrors one CSV line.

```java
public record ProductCsv(String name,
                         String description,
                         Long price,
                         String category) {
}
```

### Write the paginated query


### Implement `ItemReader<ProductCsv>`

In `ExportProductJob`, we use `MyBatisPagingItemReader` built with its builder.

```java
@Bean("exportReader")
public MyBatisPagingItemReader<ProductCsv> exportReader(SqlSessionFactory sqlSessionFactory) {
    return new MyBatisPagingItemReaderBuilder<ProductCsv>()
            .sqlSessionFactory(sqlSessionFactory)
            .queryId("com.myp.jobs.infrastructure.outbound.database.mappers.ProductMapper.findPage")
            .pageSize(PAGE_SIZE)
            .build();
}
```

`MyBatisPagingItemReader` runs the same query once per page and injects two parameters automatically:

* `_skiprows` : the offset of the current page
* `_pagesize` : the page size configured on the reader

The query **must** have a deterministic `ORDER BY`, otherwise pages can overlap or miss rows.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.myp.jobs.infrastructure.outbound.database.mappers.ProductMapper">

    <insert id="createAll" parameterType="java.util.List">
        ...
    </insert>

    <resultMap id="productCsvMap" type="com.myp.jobs.domain.services.jobs.load.csv.ProductCsv">
        <constructor>
            <arg column="name" javaType="java.lang.String"/>
            <arg column="description" javaType="java.lang.String"/>
            <arg column="price" javaType="java.lang.Long"/>
            <arg column="category" javaType="java.lang.String"/>
        </constructor>
    </resultMap>

    <select id="findPage" resultMap="productCsvMap">
        SELECT product.name,
        product.description,
        product.price,
        product_category.code AS category
        FROM product
        JOIN product_category ON product_category.id = product.category_id
        ORDER BY product.id
        LIMIT #{_pagesize} OFFSET #{_skiprows}
    </select>

</mapper>
```
### Implement `ItemWriter<ProductCsv>`

`FlatFileItemWriter` writes each chunk to the file. The header is written once, at the beginning of the file.

```java
@Bean("exportWriter")
public FlatFileItemWriter<ProductCsv> exportWriter() {
    return new FlatFileItemWriterBuilder<ProductCsv>()
            .name("productCsvWriter")
            .resource(new FileSystemResource("output/products-export.csv"))
            .shouldDeleteIfExists(true)   // overwrite the file when it already exists
            .encoding("UTF-8")
            .headerCallback(writer -> writer.write("name,description,price,category"))
            .delimited()
            .delimiter(",")
            .fieldExtractor(item -> new Object[]{
                    item.name(),
                    item.description(),
                    item.price(),
                    item.category()
            })
            .build();
}
```

If a description can contain a comma or a quote, configure `.quoteCharacter("\"")` on the delimited builder so
the values are escaped correctly.

### Wire the Step and the Job

Once we have the reader and the writer, we wire them into a Step. The chunk (1000) setting means: read 1000 items (one
page), write them, and commit one transaction.

```java

@Configuration
public class ExportProductJob {
    private final Integer PAGE_SIZE = 10000;

    @Bean
    public Job exportProducts(JobRepository jobRepository,
                              @Qualifier("exportProductsToCsvStep") Step exportProductsToCsv) {
        return new JobBuilder("exportProducts", jobRepository)
                .start(exportProductsToCsv)
                .build();
    }

    @Bean("exportProductsToCsvStep")
    public Step exportProductsToCsv(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    @Qualifier("exportReader") MyBatisPagingItemReader<ProductCsv> reader,
                                    @Qualifier("exportWriter") FlatFileItemWriter<ProductCsv> writer) {
        return new StepBuilder("exportProductsToCsv", jobRepository)
                .<ProductCsv, ProductCsv>chunk(PAGE_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean("exportReader")
    public MyBatisPagingItemReader<ProductCsv> exportReader(SqlSessionFactory sqlSessionFactory) {
        ...
    }

    @Bean("exportWriter")
    public FlatFileItemWriter<ProductCsv> exportWriter() {
        ...
    }
}
```

## Execute job and check results

### Job execution

```sql
SELECT ji.job_name, je.*
FROM batch_job_execution je
         JOIN batch_job_instance ji ON ji.job_instance_id = je.job_instance_id
WHERE ji.job_name = 'exportProducts'
ORDER BY je.job_execution_id DESC;
```

### Step execution

Check that `read_count` equals `write_count`, and that `commit_count` matches the number of pages.

```sql
SELECT step_name, read_count, write_count, commit_count, status
FROM batch_step_execution
WHERE step_name = 'exportProductsToCsv'
ORDER BY step_execution_id DESC;
```

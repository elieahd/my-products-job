For this feature, we will need to modify the `loadProducts` Job that reads products from a CSV file, validates and
transform them and writes them to the database in chunks.

### Representation of CSV

A record that mirrors one CSV line.
It is a plain data holder with no business logic.

```java
public record ProductCsv(String name,
                         String description,
                         BigDecimal price,
                         String category) {
}
```

```
ItemReader<ProductCsv> → ItemProcessor<ProductCsv, Product> → ItemWriter<Product>
```

### Implement `ItemReader<ProductCsv>`

In `LoadProductJob`, we use `FlatFileItemReader` to parse each line into a ProductCsv.

```java
@Bean("csvReader")
public ItemReader<ProductCsv> reader() {
    return new FlatFileItemReaderBuilder<ProductCsv>()
            .name("productCsvReader")
            .resource(new ClassPathResource("data/products.csv"))
            .linesToSkip(1)  // skip header row
            .delimited()
            .delimiter(",")
            .names("name", "description", "price", "category")
            .targetType(ProductCsv.class)
            .build();
}
```

### Implement `ItemProcessor<ProductCsv, Product>`

The processor validates each item and maps it to the domain `Product`. Returning null filters the item out, so it never
reaches the writer.

```java

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
```

### Implement `ItemWriter<Product>`

The writer receives a whole chunk and persists it in one go

```java

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

```

Once we have implemented the ItemReader, ItemProcessor, and ItemWriter, we wire them into a Step. The chunk (1000)
setting means: read and process 10000 items, then write them and commit one transaction.

```java

import org.springframework.context.annotation.Bean;

@Configuration
public class LoadProductJob {

    @Bean
    public Job loadProducts(JobRepository jobRepository,
                            @Qualifier("loadProductsFromCsv") Step loadProductsFromCsv) {
        return new JobBuilder("loadProducts", jobRepository)
                .start(loadProductsFromCsv)
                .build();
    }

    @Bean("loadProductsFromCsvStep")
    public Step loadProductsFromCsv(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    @Qualifier("csvReader") ItemReader<ProductCsv> reader,
                                    ItemProcessor<ProductCsv, Product> processor,
                                    ItemWriter<Product> writer) {
        return new StepBuilder("loadProductsFromCsv", jobRepository)
                .<ProductCsv, Product>chunk(10000)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean("csvReader")
    public ItemReader<ProductCsv> reader() {...}
}
```

## Execute job and check results in database

Observe the database to see the results of the job execution.
Some queries to execute :

### Loaded products

```sql
SELECT c.name AS category, COUNT(*) AS total
FROM product p
         JOIN product_category c ON c.id = p.category_id
GROUP BY c.name;
```

### Job execution

```sql
SELECT ji.job_name, je.*
FROM batch_job_execution je
         JOIN batch_job_instance ji ON ji.job_instance_id = je.job_instance_id
ORDER BY je.job_execution_id DESC;
```

### Step execution

```sql
SELECT *
FROM batch_step_execution
ORDER BY step_execution_id DESC;
```

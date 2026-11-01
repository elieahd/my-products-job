# Spring Batch 101

Spring Batch is a framework used to process large volumes of data in batches.

It is commonly used for ETL, report generation, data migrations, and bulk file imports.

## Core Concepts

### Job & Step 
A `Job` is the complete batch process. It is made of one or more `Step`

For example, a **loadProducts** job could have three steps: **download file** → **process file** → **send email**.

Example of a `Job` : Load Products Job with steps **download file** → **process file** → **send email**

```java

@Bean
public Job loadProducts(JobRepository jobRepository,
                        @Qualifier("downloadFile") Step downloadFile,
                        @Qualifier("processFile") Step processFile,
                        @Qualifier("sendEmail") Step sendEmail) {
    return new JobBuilder("loadProducts", jobRepository)
            .start(downloadFile)
            .next(processFile)
            .next(sendEmail)
            .build();
}
```

### Launching a Job 
`JobOperator` is responsible for launching a `Job`

```java

@Service
public class ProductService {
    private final Job loadProductsJob;
    private final JobOperator jobOperator;

    public ProductService(JobOperator jobOperator,
                          @Qualifier("loadProducts") Job loadProductsJob) {
        this.jobOperator = jobOperator;
        this.loadProductsJob = loadProductsJob;
    }

    public void loadProducts() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("startTime", LocalDateTime.now())
                // add other job parameters if needed 
                .toJobParameters();

        jobOperator.start(loadProductsJob, parameters);
    }
}
```
### `JobInstance`, `JobExecution` and `StepExecution`

A `JobInstance` is a Job combined with its identifying `JobParameters`
A `JobExecution` is a single attempt to run a `JobInstance` (you have the possibility to retry the job instance, so each attempt it's a new `JobExecution`)
A `StepExecution` is a single attempt to run one Step within a `JobExecution`.

### `JobRepository`

The JobRepository records all of this metadata (instances, executions, step progress, status) in database tables. 

It is what makes restartability possible, and you can query it for the JobInstances and JobExecutions of a given Job.

### Two Ways to Implement a Step

#### Tasklet

A `Tasklet` is a single-method interface that runs one task, repeatedly if needed, until it signals completion.
It suits simple work such as downloading a file or cleaning up a directory.

```java
@Bean
public Step downloadFile(JobRepository jobRepository,
                         PlatformTransactionManager txManager,
                         FileDownloader downloader) {
    return new StepBuilder("downloadFile", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                downloader.download();
                return RepeatStatus.FINISHED;
            }, txManager)
            .build();
}
```
#### Chunk-oriented processing

Chunk-oriented processing follows a `read` → `process` → `write` pattern.

* An `ItemReader` reads items one at a time
* An `ItemProcessor` transforms them one at a time
* An `ItemWriter` writes them together as a chunk

```java
@Bean
public Step processFile(JobRepository jobRepository,
                        PlatformTransactionManager txManager,
                        ItemReader<ProductCsv> reader,
                        ItemProcessor<ProductCsv, Product> processor,
                        ItemWriter<Product> writer) {
return new StepBuilder("processFile", jobRepository)
    .<ProductCsv, Product>chunk(100, txManager)
    .reader(reader)
    .processor(processor)
    .writer(writer)
    .build();
}
```
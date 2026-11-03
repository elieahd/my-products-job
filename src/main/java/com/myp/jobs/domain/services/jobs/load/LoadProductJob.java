package com.myp.jobs.domain.services.jobs.load;

import com.myp.jobs.domain.models.Product;
import com.myp.jobs.domain.services.jobs.load.csv.ProductCsv;
import com.myp.jobs.domain.services.jobs.load.steps.DoNothingTasklet;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadProductJob {

    @Bean
    public Job loadProducts(JobRepository jobRepository,
                            @Qualifier("doNothingStep") Step doNothingStep,
                            @Qualifier("loadProductsStep") Step loadProductsStep) {
        return new JobBuilder("loadProducts", jobRepository)
                .start(doNothingStep)
                .next(loadProductsStep)
                .build();
    }

    @Bean("doNothingStep")
    public Step cleanupStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            DoNothingTasklet doNothingTasklet) {
        return new StepBuilder("doNothingStep", jobRepository)
                .tasklet(doNothingTasklet, transactionManager)
                .build();
    }

    @Bean("loadProductsStep")
    public Step loadProductsStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 @Qualifier("productsFileCsvReader") ItemReader<ProductCsv> reader,
                                 ItemProcessor<ProductCsv, Product> processor,
                                 ItemWriter<Product> writer) {
        return new StepBuilder("loadProductsStep", jobRepository)
                .<ProductCsv, Product>chunk(10000)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean("productsFileCsvReader")
    public ItemReader<ProductCsv> reader() {
        return new FlatFileItemReaderBuilder<ProductCsv>()
                .name("productCsvReader")
                .resource(new ClassPathResource("data/products.csv"))
                .encoding("UTF-8")
                .linesToSkip(1)  // skip header row
                .delimited()
                .delimiter(",")
                .names("name", "description", "price", "category")
                .targetType(ProductCsv.class)
                .build();
    }

}

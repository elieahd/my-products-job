package com.myp.jobs.domain.services.jobs.export;

import com.myp.jobs.domain.services.jobs.load.csv.ProductCsv;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ExportProductJob {

    private static final int PAGE_SIZE = 10_000;

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
                                    @Qualifier("productsDBReader") MyBatisPagingItemReader<ProductCsv> reader,
                                    @Qualifier("productsCsvWriter") FlatFileItemWriter<ProductCsv> writer) {
        return new StepBuilder("exportProductsToCsv", jobRepository)
                .<ProductCsv, ProductCsv>chunk(PAGE_SIZE)
                .transactionManager(transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean("productsDBReader")
    public MyBatisPagingItemReader<ProductCsv> exportReader(SqlSessionFactory sqlSessionFactory) {
        return new MyBatisPagingItemReaderBuilder<ProductCsv>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.myp.jobs.infrastructure.outbound.database.mappers.ProductMapper.findPage")
                .pageSize(PAGE_SIZE)
                .build();
    }

    @Bean("productsCsvWriter")
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
}
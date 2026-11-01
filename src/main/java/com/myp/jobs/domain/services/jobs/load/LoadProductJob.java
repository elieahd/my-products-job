package com.myp.jobs.domain.services.jobs.load;

import com.myp.jobs.domain.services.jobs.load.steps.DoNothingTasklet;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadProductJob {

    @Bean
    public Job loadProducts(JobRepository jobRepository,
                            @Qualifier("doNothingStep") Step doNothingStep) {
        return new JobBuilder("loadProducts", jobRepository)
                .start(doNothingStep)
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

}

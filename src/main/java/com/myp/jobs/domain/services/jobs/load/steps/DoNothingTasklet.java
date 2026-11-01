package com.myp.jobs.domain.services.jobs.load.steps;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class DoNothingTasklet implements Tasklet {

    private final Logger log = LoggerFactory.getLogger(DoNothingTasklet.class);

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Hello! I am a tasklet, I am doing nothing!");
        return RepeatStatus.FINISHED;
    }

}

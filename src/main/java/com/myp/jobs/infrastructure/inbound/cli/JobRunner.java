package com.myp.jobs.infrastructure.inbound.cli;

import com.myp.jobs.domain.services.JobService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class JobRunner implements ApplicationRunner {

    private final JobService jobService;

    public JobRunner(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String jobName = args.getNonOptionArgs()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Job name is required"));
        jobService.start(jobName);
    }
}
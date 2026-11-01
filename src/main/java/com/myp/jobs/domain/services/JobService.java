package com.myp.jobs.domain.services;

import com.myp.jobs.domain.inbound.GetJobs;
import com.myp.jobs.domain.inbound.RunJob;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
public class JobService implements GetJobs, RunJob {

    private final Map<String, Job> jobs;
    private final JobOperator jobOperator;

    public JobService(Map<String, Job> jobs,
                      JobOperator jobOperator) {
        this.jobs = jobs;
        this.jobOperator = jobOperator;
    }

    @Override
    public Set<String> getJobNames() {
        return jobs.keySet();
    }

    @Override
    public void start(String jobName) throws Exception {
        if (!jobs.containsKey(jobName)) {
            throw new IllegalArgumentException("Job with name " + jobName + " does not exist");
        }

        Job job = jobs.get(jobName);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("startTime", LocalDateTime.now())
                .toJobParameters();
        jobOperator.start(job, jobParameters);
    }

}

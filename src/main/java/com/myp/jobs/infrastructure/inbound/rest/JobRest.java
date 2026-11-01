package com.myp.jobs.infrastructure.inbound.rest;

import com.myp.jobs.domain.inbound.GetJobs;
import com.myp.jobs.domain.inbound.RunJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Tag(name = "Jobs", description = "Jobs functionality")
@RequestMapping("api/v1/jobs")
public class JobRest {

    private final GetJobs getJobs;
    private final RunJob runJob;

    public JobRest(GetJobs getJobs, RunJob runJob) {
        this.getJobs = getJobs;
        this.runJob = runJob;
    }

    @GetMapping
    @Operation(summary = "Job Names", description = "Retrieve all job names", responses = {@ApiResponse(responseCode = "200", description = "Job names", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))})
    public ResponseEntity<Set<String>> getAllJobNames() {
        Set<String> jobNames = getJobs.getJobNames();
        return ResponseEntity.ok(jobNames);
    }

    @PostMapping("{jobName}")
    @Operation(summary = "Start job", description = "Start job")
    public ResponseEntity<Void> startJob(@PathVariable String jobName) throws Exception {
        runJob.start(jobName);
        return ResponseEntity.noContent().build();
    }

}

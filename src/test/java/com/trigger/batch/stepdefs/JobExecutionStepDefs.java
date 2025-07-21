package com.trigger.batch.stepdefs;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class JobExecutionStepDefs {

    @Autowired
    private List<Job> jobs;

    @Autowired
    private JobLauncher jobLauncher;

    private JobExecution jobExecution;

    @When("I run the {string} job")
    public void i_run_the_job(String jobName) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis()) // for uniqueness
                .toJobParameters();

    
        var jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJob(jobs.stream().filter(it -> it.getName().equals(jobName)).findFirst().get());
        jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertNotNull(jobExecution, "JobExecution should not be null");
    }

    @Then("the job should end with status {string}")
    public void the_job_should_end_with_status(String expectedStatus) {
        assertNotNull(jobExecution, "JobExecution must be initialized before checking status");

        BatchStatus actualStatus = jobExecution.getStatus();
        assertEquals(expectedStatus.toUpperCase(), actualStatus.name(),
                () -> "Expected job status to be " + expectedStatus + " but was " + actualStatus);
    }
}

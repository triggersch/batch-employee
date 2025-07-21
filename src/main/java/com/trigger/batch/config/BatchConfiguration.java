package com.trigger.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.trigger.batch.listener.CompanyStepListener;
import com.trigger.batch.model.Company;
import com.trigger.batch.step.CompaniesJsonReader;
import com.trigger.batch.step.CompanyItemProcessor;
import com.trigger.batch.step.CompanyItemWriter;

@Configuration
public class BatchConfiguration {

    @Bean
    public Job importCompanies(JobRepository jobRepository,
                               Step importStep) {
        return new JobBuilder("importCompanies", jobRepository)
                .start(importStep)
                .build();
    }

    @Bean
    public Step importStep(JobRepository jobRepository,
    PlatformTransactionManager transactionManager,
                           CompaniesJsonReader reader,
                           CompanyItemProcessor processor,
                           CompanyItemWriter writer,
                            CompanyStepListener stepListener) {
        return new StepBuilder("importStep", jobRepository)
                .<Company, Company>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepListener)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

}


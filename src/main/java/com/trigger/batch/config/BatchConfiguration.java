package com.trigger.batch.config;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.trigger.batch.listener.CompanyStepListener;
import com.trigger.batch.model.Company;
import com.trigger.batch.partitioner.SplitFilePartitioner;
import com.trigger.batch.step.CobolFlatFileItemReader;
import com.trigger.batch.step.CompanyItemProcessor;
import com.trigger.batch.step.CompanyItemWriter;

@Configuration
public class BatchConfiguration {

    @Autowired
    private Tasklet cobolFlatFileSplitterTasklet;

    @Bean
    public Job importCompanies(JobRepository jobRepository,
            Step splitFlatFileStep,
            Step processSplitFilesStep) {
        return new JobBuilder("importCompanies", jobRepository)
                .start(splitFlatFileStep)
                .next(processSplitFilesStep)
                .build();
    }

    
    @Bean
    public Job serializeCompanies(
                    JobRepository jobRepository,
                    Step serializeStep) {
            return new JobBuilder("serializeCompanies", jobRepository)
                            .start(serializeStep)
                            .build();
    }

    @Bean
    public Step splitFlatFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("splitFlatFileStep", jobRepository)
                .tasklet(cobolFlatFileSplitterTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step processSplitFilesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SplitFilePartitioner partitioner,
            Step importStep) {
        return new StepBuilder("processSplitFilesStep", jobRepository)
                .partitioner(importStep.getName(), partitioner)
                .step(importStep)
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step importStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CobolFlatFileItemReader reader,
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
    public Step serializeStep(
                    JobRepository jobRepository,
                    PlatformTransactionManager transactionManager,
                    JdbcPagingItemReader<Company> reader,
                    FlatFileItemWriter<Company> writer) {
            return new StepBuilder("serializeStep", jobRepository)
                            .<Company, Company>chunk(5, transactionManager)
                            .reader(reader)
                            .writer(writer)
                            .build();
    }

    @Bean
    public FlatFileItemWriter<Company> companyRecordWriter(BatchProperties batchProperties) {
            FlatFileItemWriter<Company> writer = new FlatFileItemWriter<>();

            Path outputFile = Path.of(batchProperties.getOutput(), "companies.dat");
            writer.setResource(new FileSystemResource(outputFile));
            writer.setAppendAllowed(false);

            FormatterLineAggregator<Company> lineAggregator = new FormatterLineAggregator<>();
            lineAggregator.setFormat("%-10s%-30s%-30s%-20s");

            BeanWrapperFieldExtractor<Company> fieldExtractor = new BeanWrapperFieldExtractor<>();
            fieldExtractor.setNames(new String[] { "id", "name", "location", "industry" });
            lineAggregator.setFieldExtractor(fieldExtractor);

            writer.setLineAggregator(lineAggregator);

            return writer;
    }

    @Bean
    public JdbcPagingItemReader<Company> companyReader(DataSource dataSource) throws Exception {
            JdbcPagingItemReader<Company> reader = new JdbcPagingItemReader<>();
            reader.setDataSource(dataSource);
            reader.setPageSize(100);
            reader.setRowMapper(new BeanPropertyRowMapper<>(Company.class));

            SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
            queryProvider.setDataSource(dataSource);
            queryProvider.setSelectClause("id, name, location, industry");
            queryProvider.setFromClause("from COMPANIES");
            queryProvider.setSortKey("id"); // obligatoire pour pagination

            reader.setQueryProvider(queryProvider.getObject());

            return reader;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

}

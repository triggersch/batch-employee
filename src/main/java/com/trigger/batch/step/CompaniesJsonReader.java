package com.trigger.batch.step;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trigger.batch.model.Company;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@StepScope
@Component
public class CompaniesJsonReader implements ItemReader<Company> {

    private final ObjectMapper objectMapper;

    private String filePath;

    private Iterator<Company> companyIterator;

    public CompaniesJsonReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

     @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.filePath = stepExecution.getExecutionContext().getString("filePath");
    }

    @Override
    public Company read() throws Exception {
        if (companyIterator == null) {
            Map<String, List<Company>> wrapper = objectMapper.readValue(new File(filePath), new TypeReference<>() {});
            companyIterator = wrapper.get("companies").iterator();
        }
        return companyIterator.hasNext() ? companyIterator.next() : null;
    }
}

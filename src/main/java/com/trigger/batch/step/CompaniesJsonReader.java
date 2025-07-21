package com.trigger.batch.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trigger.batch.model.Company;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.List;

@Component
public class CompaniesJsonReader implements ItemReader<Company> {

    private final ObjectMapper objectMapper;

    private final String inputJsonPath;

    private Iterator<Company> companyIterator;

    public CompaniesJsonReader(ObjectMapper objectMapper, @Value("${batch.files.paths.input.json}") String inputJsonPath) {
        this.objectMapper = objectMapper;
        this.inputJsonPath = inputJsonPath;
    }

    @Override
    public Company read() throws Exception {
        if (companyIterator == null) {
            File file = new File(inputJsonPath);
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode companiesNode = rootNode.get("companies");
             if (companiesNode == null || !companiesNode.isArray()) {
                throw new IllegalStateException("Le fichier JSON ne contient pas le champ 'companies' ou n'est pas un tableau.");
            }
            List<Company> companies = List.of(
                    objectMapper.treeToValue(companiesNode, Company[].class)
            );
            companyIterator = companies.iterator();
        }

        return companyIterator.hasNext() ? companyIterator.next() : null;
    }
}

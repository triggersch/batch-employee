package com.trigger.batch.step;

import com.trigger.batch.model.Company;
import com.trigger.batch.model.Employee;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.batch.core.configuration.annotation.StepScope;

import java.io.BufferedReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@StepScope
@Component
public class CobolFlatFileItemReader implements ItemReader<Company> {

    @Value("#{stepExecutionContext['filePath']}")
    private String filePath;

    private Iterator<Company> companyIterator;

    @Override
    public Company read() throws Exception {
        if (companyIterator == null) {
            List<Company> companies = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
                String line;
                Company currentCompany = null;
                while ((line = reader.readLine()) != null) {
                    char recType = line.charAt(0);
                    if (recType == 'C') {
                        currentCompany = new Company();
                        currentCompany.setId(line.substring(1, 11).trim());
                        currentCompany.setName(line.substring(11, 41).trim());
                        currentCompany.setLocation(line.substring(41, 71).trim());
                        currentCompany.setIndustry(line.substring(71, 91).trim());
                        currentCompany.setEmployees(new ArrayList<>());
                        companies.add(currentCompany);
                    } else if (recType == 'E') {
                        if (currentCompany == null) {
                            throw new IllegalStateException("Employee without company");
                        }
                        Employee emp = new Employee();
                        emp.setId(line.substring(1, 11).trim());
                        emp.setName(line.substring(11, 41).trim());
                        emp.setPosition(line.substring(41, 66).trim());
                        emp.setEmail(line.substring(66, 103).trim());
                        emp.setCompanyId(line.substring(103, line.length()).trim());
                        currentCompany.getEmployees().add(emp);
                    }
                }
            }
            companyIterator = companies.iterator();
        }

        return companyIterator.hasNext() ? companyIterator.next() : null;
    }
}

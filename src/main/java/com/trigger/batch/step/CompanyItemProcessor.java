package com.trigger.batch.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.trigger.batch.model.Company;

@Component
public class CompanyItemProcessor implements ItemProcessor<Company, Company> {

    @Override
    public Company process(@NonNull Company company) {
        if (company.getEmployees() != null) {
            company.getEmployees().forEach(employee -> {
                employee.setCompanyId(company.getId());
            });
        }
        return company;
    }
}


package com.trigger.batch.step;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.trigger.batch.listener.EmployeeStepListener;
import com.trigger.batch.model.Company;
import com.trigger.batch.model.Employee;

import java.util.List;

@Component
public class CompanyItemWriter implements ItemWriter<Company> {

    private final JdbcBatchItemWriter<Company> companyWriter;
    private final JdbcBatchItemWriter<Employee> employeeWriter;
    private final EmployeeStepListener employeeStepListener;

    public CompanyItemWriter(NamedParameterJdbcTemplate jdbcTemplate, EmployeeStepListener employeeStepListener) {

           this.employeeStepListener = employeeStepListener;
        // Writer pour la table COMPANIES
        this.companyWriter = new JdbcBatchItemWriter<>();
        companyWriter.setJdbcTemplate(jdbcTemplate);
        companyWriter.setSql("""
            INSERT INTO COMPANIES (id, name, location, industry)
            VALUES (:id, :name, :location, :industry)
        """);
        companyWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        companyWriter.afterPropertiesSet();

        // Writer pour la table EMPLOYEES
        this.employeeWriter = new JdbcBatchItemWriter<>();
        employeeWriter.setJdbcTemplate(jdbcTemplate);
        employeeWriter.setSql("""
            INSERT INTO EMPLOYEES (id, name, position, email, company_id)
            VALUES (:id, :name, :position, :email, :companyId)
        """);
        employeeWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        employeeWriter.afterPropertiesSet();
    }

    @Override
    public void write(@NonNull Chunk<? extends Company> chunk) throws Exception {

        // Écrire les companies
        companyWriter.write(chunk);


       Employee[] allEmployees = chunk.getItems().stream().flatMap(company -> 
            company.getEmployees().stream()
        ).toArray(Employee[]::new);

        // Écrire les employés
        employeeWriter.write(Chunk.of(allEmployees));

        this.employeeStepListener.notify(List.of(allEmployees));
    }

}


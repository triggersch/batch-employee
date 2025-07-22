package com.trigger.batch.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseVerificationStepDefs {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Then("the following companies should be registered:")
    public void the_following_companies_should_be_registered(DataTable expectedTable) {
        List<Map<String, String>> expected = expectedTable.asMaps();

        // Fetch all companies from DB
        List<Map<String, Object>> actualCompanies = jdbcTemplate.queryForList("SELECT * FROM COMPANIES");

        assertEquals(expected.size(), actualCompanies.size(), "Number of companies doesn't match");

        for (Map<String, String> expectedRow : expected) {
            boolean found = actualCompanies.stream()
                    .anyMatch(actual -> actual.get("id").equals(expectedRow.get("id")) &&
                            actual.get("name").equals(expectedRow.get("name")) &&
                            actual.get("location").equals(expectedRow.get("location")) &&
                            actual.get("industry").equals(expectedRow.get("industry")));

            assertTrue(found, () -> "Company not found in DB: " + expectedRow);
        }
    }

    @Then("the following employees should be registered:")
    public void the_following_employees_should_be_registered(DataTable expectedTable) {
        List<Map<String, String>> expected = expectedTable.asMaps();

        // Fetch all employees from DB
        List<Map<String, Object>> actualEmployees = jdbcTemplate.queryForList("SELECT * FROM EMPLOYEES");

        assertEquals(expected.size(), actualEmployees.size(), "Number of employees doesn't match");

        for (Map<String, String> expectedRow : expected) {
            boolean found = actualEmployees.stream()
                    .anyMatch(actual -> actual.get("id").equals(expectedRow.get("id")) &&
                            actual.get("name").equals(expectedRow.get("name")) &&
                            actual.get("position").equals(expectedRow.get("position")) &&
                            actual.get("email").equals(expectedRow.get("email")) &&
                            actual.get("company_id").equals(expectedRow.get("companyId")));

            assertTrue(found, () -> "Employee not found in DB: " + expectedRow);
        }
    }

    @Then("exactly {int} companies should be present in the database")
    public void exactly_n_companies_should_be_present_in_the_database(int expectedCount) {
        Integer actualCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM COMPANIES", Integer.class);
        assertEquals(expectedCount, actualCount);
    }

    @Then("exactly {int} employees should be registered in the database")
    public void check_employees_count(int expectedCount) {
        Integer actualCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM EMPLOYEES", Integer.class);
        assertEquals(expectedCount, actualCount);
    }
}

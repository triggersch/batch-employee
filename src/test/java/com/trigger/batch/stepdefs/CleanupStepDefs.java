package com.trigger.batch.stepdefs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.Before;

public class CleanupStepDefs {

    @Autowired
    private JdbcTemplate jdbcTemplate;

   @Before
    public void cleanDatabase() {
        // Désactiver contraintes FK avant de nettoyer (si besoin)
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        jdbcTemplate.update("DELETE FROM EMPLOYEES");
        jdbcTemplate.update("DELETE FROM COMPANIES");

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}

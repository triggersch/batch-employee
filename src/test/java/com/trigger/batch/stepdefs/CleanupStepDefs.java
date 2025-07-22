package com.trigger.batch.stepdefs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import io.cucumber.java.Before;

public class CleanupStepDefs {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${batch.files.paths.split.dir}")
    private String inputSplitDir;

   @Before
    public void cleanDatabase() {
        // Désactiver contraintes FK avant de nettoyer (si besoin)
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        jdbcTemplate.update("DELETE FROM EMPLOYEES");
        jdbcTemplate.update("DELETE FROM COMPANIES");

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

     @Before
    public void cleanSplitDirectory() throws IOException {
        Path splitDir = Path.of(inputSplitDir);
        if (Files.exists(splitDir)) {
            Files.list(splitDir)
                .filter(path -> path.getFileName().toString().startsWith("companies_") && path.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors du nettoyage du dossier de split : " + path, e);
                    }
                });
        }
    }
}
